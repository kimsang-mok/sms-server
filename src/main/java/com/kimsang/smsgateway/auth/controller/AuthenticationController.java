package com.kimsang.smsgateway.auth.controller;

import com.kimsang.smsgateway.auth.dto.AuthenticationRequestDto;
import com.kimsang.smsgateway.auth.dto.AuthenticationResponseDto;
import com.kimsang.smsgateway.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService authenticationService;

  @PostMapping("/login")
  public Mono<ResponseEntity<AuthenticationResponseDto>> authenticate(@RequestBody final AuthenticationRequestDto request) {
    return authenticationService.authenticate(request).map(ResponseEntity::ok);
  }

  @PostMapping("/refresh-token")
  public Mono<ResponseEntity<AuthenticationResponseDto>> refreshToken(@RequestParam UUID refreshToken) {
    return authenticationService.refreshToken(refreshToken).map(ResponseEntity::ok);
  }

  @PostMapping("/logout")
  public Mono<ResponseEntity<Void>> revokeToken(@RequestParam UUID refreshToken) {
    return authenticationService.revokeRefreshToken(refreshToken).thenReturn(ResponseEntity.noContent().build());
  }
}
