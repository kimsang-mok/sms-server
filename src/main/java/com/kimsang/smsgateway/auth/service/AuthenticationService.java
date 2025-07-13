package com.kimsang.smsgateway.auth.service;

import com.kimsang.smsgateway.auth.dto.AuthenticationRequestDto;
import com.kimsang.smsgateway.auth.dto.AuthenticationResponseDto;
import com.kimsang.smsgateway.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final ReactiveAuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public Mono<AuthenticationResponseDto> authenticate(final AuthenticationRequestDto request) {
    final var authToken = UsernamePasswordAuthenticationToken.unauthenticated(request.username(),
        request.password());
    return authenticationManager.authenticate(authToken)
        .flatMap(auth -> {
          final var token = jwtService.generateToken(request.username());
          return Mono.just(new AuthenticationResponseDto(token));
        });
  }
}
