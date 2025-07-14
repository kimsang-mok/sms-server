package com.kimsang.smsgateway.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimsang.smsgateway.common.util.ProblemDetailWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class BearerTokenAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
  private final ObjectMapper objectMapper;

  @Override
  public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
    final var status = HttpStatus.UNAUTHORIZED;

    log.info("{}: {}", status.getReasonPhrase(), ex.getMessage());

    return ProblemDetailWriter.write(exchange, HttpStatus.UNAUTHORIZED, ex.getMessage(), objectMapper);
  }
}
