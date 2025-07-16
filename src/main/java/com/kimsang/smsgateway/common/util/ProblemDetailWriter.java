package com.kimsang.smsgateway.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@UtilityClass
public class ProblemDetailWriter {
  public Mono<Void> write(ServerWebExchange exchange, HttpStatus status, String message, ObjectMapper objectMapper) {
    var response = exchange.getResponse();
    response.setStatusCode(status);
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

    var problem = ProblemDetail.forStatusAndDetail(status, message);

    return response.writeWith(Mono.fromSupplier(() -> {
      try {
        var buffer = objectMapper.writeValueAsBytes(problem);
        return response.bufferFactory().wrap(buffer);
      } catch (Exception ex) {
        return response.bufferFactory().wrap(new byte[0]);
      }
    }));
  }
}
