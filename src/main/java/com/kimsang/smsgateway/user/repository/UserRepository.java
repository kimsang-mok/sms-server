package com.kimsang.smsgateway.user.repository;

import com.kimsang.smsgateway.user.domain.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveCrudRepository<User, UUID> {
  Mono<User> findByEmail(String email);

  Mono<User> findByUsername(String username);

  Mono<Boolean> existsByEmail(String email);

  Mono<Boolean> existsByUsername(String username);
}
