package com.kimsang.smsgateway.auth.security;

import com.kimsang.smsgateway.auth.exception.EmailVerificationException;
import com.kimsang.smsgateway.common.exception.NotFoundException;
import com.kimsang.smsgateway.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CustomReactiveAuthenticationManager implements ReactiveAuthenticationManager {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Mono<Authentication> authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String rawPassword = authentication.getCredentials().toString();

    return userRepository.findByEmail(username)
        .switchIfEmpty(userRepository.findByUsername(username))
        .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
        .flatMap(user -> {
          if (!user.isActive()) {
            return Mono.error(new DisabledException("User is inactive"));
          }

          if (!user.isVerified()) {
            return Mono.error(new EmailVerificationException("User is not verified"));
          }

          if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            return Mono.error(new BadCredentialsException("Invalid credentials"));
          }

          UserDetails principal = new CustomUserDetails(user);

          Authentication auth = new UsernamePasswordAuthenticationToken(
              principal, null, principal.getAuthorities());

          return Mono.just(auth);
        });
  }
}
