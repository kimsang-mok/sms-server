package com.kimsang.smsgateway.auth.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("refresh_tokens")
public class RefreshToken {
  @Id
  private UUID id;

  private UUID userId;
  private Instant createdAt;
  private Instant expiresAt;
}
