package com.kimsang.smsgateway.auth.service;

import com.kimsang.smsgateway.auth.dto.AuthenticationRequestDto;
import com.kimsang.smsgateway.auth.dto.AuthenticationResponseDto;
import com.kimsang.smsgateway.auth.entity.RefreshToken;
import com.kimsang.smsgateway.auth.repository.RefreshTokenRepository;
import com.kimsang.smsgateway.auth.security.JwtService;
import com.kimsang.smsgateway.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final ReactiveAuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  private final R2dbcEntityTemplate entityTemplate;

  @Value("${jwt.refresh-token-ttl}")
  private Duration refreshTokenTtl;

  public Mono<AuthenticationResponseDto> authenticate(AuthenticationRequestDto request) {
    return authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()))
        .flatMap(auth ->
            userRepository.findByUsername(request.username())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED)))
        )
        .flatMap(user -> {
          String accessToken = jwtService.generateToken(user.getUsername());
          RefreshToken refreshToken = new RefreshToken();
          refreshToken.setId(UUID.randomUUID());
          refreshToken.setUserId(user.getId());
          refreshToken.setExpiresAt(Instant.now().plus(refreshTokenTtl));

          return entityTemplate.insert(RefreshToken.class).using(refreshToken)
              .map(savedToken -> new AuthenticationResponseDto(accessToken, savedToken.getId()));
        });
  }

  public Mono<AuthenticationResponseDto> refreshToken(UUID refreshToken) {
    return refreshTokenRepository.findByIdAndExpiresAtAfter(refreshToken, Instant.now())
        .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid or expired refresh token")))
        .flatMap(token ->
            userRepository.findById(token.getUserId())
                .switchIfEmpty(Mono.error(new BadCredentialsException("User not found")))
                .map(user -> {
                  final var newAccessToken = jwtService.generateToken(user.getUsername());
                  return new AuthenticationResponseDto(newAccessToken, token.getId());
                })
        );
  }

  public Mono<Void> revokeRefreshToken(UUID refreshToken) {
    return refreshTokenRepository.deleteById(refreshToken);
  }
}
