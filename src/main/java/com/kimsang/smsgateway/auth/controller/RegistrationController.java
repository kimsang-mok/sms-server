package com.kimsang.smsgateway.auth.controller;

import com.kimsang.smsgateway.auth.dto.RegistrationRequestDto;
import com.kimsang.smsgateway.auth.dto.RegistrationResponseDto;
import com.kimsang.smsgateway.auth.mapper.RegistrationMapper;
import com.kimsang.smsgateway.auth.service.EmailVerificationService;
import com.kimsang.smsgateway.auth.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegistrationController {
  @Value("${email-verification.required}")
  private boolean emailVerificationRequired;

  private final RegistrationService registrationService;

  private final RegistrationMapper registrationMapper;

  private final EmailVerificationService emailVerificationService;

  @PostMapping("/register")
  public Mono<ResponseEntity<RegistrationResponseDto>> register(@Valid @RequestBody final RegistrationRequestDto request) {
    final var userEntity = registrationMapper.toEntity(request);

    return registrationService.register(userEntity)
        .flatMap(registeredUser -> {
          Mono<Void> verification = Mono.empty();
          if (emailVerificationRequired) {
            verification = emailVerificationService.sendVerificationToken(
                registeredUser.getId(),
                registeredUser.getEmail()
            );
          }

          return verification.thenReturn(
              ResponseEntity
                  .status(HttpStatus.CREATED)
                  .body(registrationMapper.toResponse(registeredUser))
          );
        });
  }
}
