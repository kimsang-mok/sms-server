package com.kimsang.smsgateway.auth.controller;

import com.kimsang.smsgateway.auth.dto.RegistrationRequestDto;
import com.kimsang.smsgateway.auth.dto.RegistrationResponseDto;
import com.kimsang.smsgateway.auth.mapper.RegistrationMapper;
import com.kimsang.smsgateway.auth.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegistrationController {
  private final RegistrationService registrationService;

  private final RegistrationMapper registrationMapper;

  @PostMapping("/register")
  public Mono<ResponseEntity<RegistrationResponseDto>> register(@Valid @RequestBody final RegistrationRequestDto request) {
    return registrationService
        .register(registrationMapper.toEntity(request))
        .map(registrationMapper::toResponse)
        .map(ResponseEntity::ok);
  }
}
