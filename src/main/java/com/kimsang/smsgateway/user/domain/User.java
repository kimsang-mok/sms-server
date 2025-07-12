package com.kimsang.smsgateway.user.domain;

import com.kimsang.smsgateway.common.domain.AuditableEntity;
import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("users")
public class User extends AuditableEntity {
  private String username;
  private String email;
  private String password;

  private UserRole role;

  private boolean verified;
  private boolean isActive;
}
