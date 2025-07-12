package com.kimsang.smsgateway.auth.mapper;

import com.kimsang.smsgateway.auth.dto.RegistrationRequestDto;
import com.kimsang.smsgateway.auth.dto.RegistrationResponseDto;
import com.kimsang.smsgateway.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class RegistrationMapper {
  public User toEntity(RegistrationRequestDto dto) {
    final User user = new User();

    user.setEmail(dto.email());
    user.setUsername(dto.username());
    user.setPassword(dto.password());

    return user;
  }

  public RegistrationResponseDto toResponse(final User user) {
    return new RegistrationResponseDto(user.getUsername(), user.getEmail());
  }
}
