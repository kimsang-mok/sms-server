package com.kimsang.smsgateway.common.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Order(-2)
@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ProblemDetail> handleValidationException(WebExchangeBindException ex, ServerWebExchange exchange) {
    Map<String, String> errors = ex.getFieldErrors().stream().collect(Collectors.toMap(
        FieldError::getField,
        fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("Invalid value"),
        (first, second) -> first // in case of duplicate key
    ));

    ProblemDetail problemDetail = ProblemDetailExt.forStatusDetailAndErrors(
        HttpStatus.BAD_REQUEST,
        "Validation Failed",
        errors);

    return Mono.just(problemDetail);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public Mono<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation error occurred" +
        ".");
    log.warn("ConstraintViolationException: {}", ex.getMessage());
    return Mono.just(problemDetail);
  }

  @ExceptionHandler(AuthenticationException.class)
  public Mono<ProblemDetail> handleAuthentication(AuthenticationException ex) {
    return Mono.just(ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage()));
  }


  @ExceptionHandler(DecodingException.class)
  public Mono<ProblemDetail> handleDecodingException(DecodingException ex) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST,
        "Malformed request payload");
    return Mono.just(problemDetail);
  }

  @ExceptionHandler(CustomValidationException.class)
  public Mono<ProblemDetail> handleCustomValidation(CustomValidationException ex) {
    return Mono.just(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage()));
  }

  @ExceptionHandler(NotFoundException.class)
  public Mono<ProblemDetail> handleNotFound(NotFoundException ex) {
    return Mono.just(ProblemDetail.forStatusAndDetail(ex.getStatus(), ex.getMessage()));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public Mono<ProblemDetail> handleResponseStatusException(ResponseStatusException ex) {
    HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
    assert status != null;
    return Mono.just(ProblemDetail.forStatusAndDetail(
        status,
        ex.getReason() != null ? ex.getReason() : "Resource not found"));
  }

  @ExceptionHandler(Exception.class)
  public Mono<ProblemDetail> handleException(Exception ex) {
    log.error("Unexpected error: ", ex);
    return Mono.just(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"));
  }
}
