package com.kimsang.smsgateway.auth.controller;

import com.kimsang.smsgateway.auth.dto.AuthenticationRequestDto;
import com.kimsang.smsgateway.auth.dto.AuthenticationResponseDto;
import com.kimsang.smsgateway.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService authenticationService;

  @PostMapping("/login")
  public Mono<ResponseEntity<AuthenticationResponseDto>> authenticate(@RequestBody final AuthenticationRequestDto request) {
    return authenticationService.authenticate(request).map(ResponseEntity::ok);
  }
}
