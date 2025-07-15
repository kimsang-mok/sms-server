package com.kimsang.smsgateway.auth.repository;

import com.kimsang.smsgateway.auth.entity.RefreshToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public interface RefreshTokenRepository extends ReactiveCrudRepository<RefreshToken, UUID> {
  Mono<RefreshToken> findByIdAndExpiresAtAfter(UUID id, Instant date);
}
