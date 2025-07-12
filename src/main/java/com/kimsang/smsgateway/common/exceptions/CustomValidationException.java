package com.kimsang.smsgateway.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class CustomValidationException extends RuntimeException {
  private final HttpStatus status;
  private final Map<String, String> errors;

  public CustomValidationException(HttpStatus status, Map<String, String> errors) {
    super("Request validation exception");
    this.status = status;
    this.errors = errors;
  }
}
