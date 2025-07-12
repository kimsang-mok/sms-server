package com.kimsang.smsgateway.auth.security;

import com.kimsang.smsgateway.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

@Getter
public class CustomUserDetails implements UserDetails {
  private final UUID id;
  private final String username;
  private final String password;
  private final List<GrantedAuthority> authorities;
  private final boolean isActive;

  public CustomUserDetails(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.password = user.getPassword();
    this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    this.isActive = user.isActive();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return isActive;
  }
}
