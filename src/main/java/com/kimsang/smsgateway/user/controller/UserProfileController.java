package com.kimsang.smsgateway.user.controller;

import com.kimsang.smsgateway.user.dto.UserProfileDto;
import com.kimsang.smsgateway.user.mapper.UserMapper;
import com.kimsang.smsgateway.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {
  private final UserService userService;
  private final UserMapper userMapper;

  @GetMapping("/me")
  public Mono<ResponseEntity<UserProfileDto>> getUserProfile(final Authentication authentication) {
    return userService
        .getUserByUsername(authentication.getName())
        .map(userMapper::toUserProfileDto)
        .map(ResponseEntity::ok);
  }
}
