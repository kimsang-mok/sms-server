package com.kimsang.smsgateway.auth.controller;

import com.kimsang.smsgateway.auth.service.EmailVerificationService;
import com.kimsang.smsgateway.user.dto.UserProfileDto;
import com.kimsang.smsgateway.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth/email")
@RequiredArgsConstructor
public class EmailVerificationController {
  private final EmailVerificationService emailVerificationService;

  private final UserMapper userMapper;

  @GetMapping("/verify")
  public Mono<ResponseEntity<UserProfileDto>> verifyEmail(
      @RequestParam("uid") String encryptedUserId,
      @RequestParam("t") String token
  ) {
    return emailVerificationService.verifyEmail(encryptedUserId, token)
        .map(userMapper::toUserProfileDto)
        .map(ResponseEntity::ok);
  }

  @PostMapping("/resend-verification")
  public Mono<ResponseEntity<Void>> resendVerification(
      @RequestParam String email
  ) {
    return emailVerificationService.resendVerificationToken(email)
        .thenReturn(ResponseEntity.noContent().build());
  }
}
