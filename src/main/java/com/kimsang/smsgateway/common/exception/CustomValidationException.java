package com.kimsang.smsgateway.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class CustomValidationException extends RuntimeException {
  private final HttpStatus status;
  private final Map<String, String> errors;

  public CustomValidationException(HttpStatus status, Map<String, String> errors) {
    super("Request validation failed");
    this.status = status;
    this.errors = errors;
  }
}
