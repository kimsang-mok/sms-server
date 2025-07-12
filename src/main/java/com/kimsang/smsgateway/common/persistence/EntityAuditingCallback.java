package com.kimsang.smsgateway.common.persistence;

import com.kimsang.smsgateway.common.domain.AuditableEntity;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class EntityAuditingCallback implements BeforeConvertCallback<AuditableEntity> {

  @Override
  public @NonNull Mono<AuditableEntity> onBeforeConvert(@NonNull AuditableEntity entity, SqlIdentifier table) {
    LocalDateTime now = LocalDateTime.now();

    if (entity.getId() == null) {
      entity.setId(UUID.randomUUID());
      entity.setCreatedAt(now);
    }
    entity.setUpdatedAt(now);

    return Mono.just(entity);
  }
}

