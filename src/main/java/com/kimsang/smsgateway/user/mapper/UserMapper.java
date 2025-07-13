package com.kimsang.smsgateway.user.mapper;

import com.kimsang.smsgateway.user.dto.UserProfileDto;
import com.kimsang.smsgateway.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
  public UserProfileDto toUserProfileDto(User user) {
    return new UserProfileDto(user.getEmail(), user.getUsername());
  }
}
