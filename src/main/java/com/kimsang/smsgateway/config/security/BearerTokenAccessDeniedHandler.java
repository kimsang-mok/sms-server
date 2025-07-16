package com.kimsang.smsgateway.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimsang.smsgateway.common.util.ProblemDetailWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class BearerTokenAccessDeniedHandler implements ServerAccessDeniedHandler {
  private final ObjectMapper objectMapper;

  public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException ex) {
    final var status = HttpStatus.FORBIDDEN;

    log.info("{}: {}", status.getReasonPhrase(), ex.getMessage());

    return ProblemDetailWriter.write(exchange, status, ex.getMessage(), objectMapper);
  }
}
