package com.kimsang.smsgateway.auth.service;

import com.kimsang.smsgateway.common.exception.CustomValidationException;
import com.kimsang.smsgateway.user.domain.User;
import com.kimsang.smsgateway.user.domain.UserRole;
import com.kimsang.smsgateway.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class RegistrationService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public Mono<User> register(User user) {
    final var errors = new HashMap<String, String>();

    return Mono.zip(
            userRepository.existsByEmail(user.getEmail()),
            userRepository.existsByUsername(user.getUsername())
        )
        .flatMap(tuple -> {
          boolean emailExists = tuple.getT1();
          boolean usernameExists = tuple.getT2();

          if (emailExists) {
            errors.put("email", "Email [%s] is already taken".formatted(user.getEmail()));
          }

          if (usernameExists) {
            errors.put("username", "Username [%s] is already taken".formatted(user.getUsername()));
          }

          if (!errors.isEmpty()) {
            return Mono.error(new CustomValidationException(HttpStatus.CONFLICT, errors));
          }

          user.setPassword(passwordEncoder.encode(user.getPassword()));
          user.setRole(UserRole.DEVELOPER);
          user.setActive(true);
          return userRepository.save(user);
        });
  }
}
