package com.kimsang.smsgateway.common.domain;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AuditableEntity {
  @Id
  private UUID id;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("updated_at")
  private LocalDateTime updatedAt;
}
