package com.kimsang.smsgateway.common.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }

  public HttpStatus getStatus() {
    return HttpStatus.NOT_FOUND;
  }
}
