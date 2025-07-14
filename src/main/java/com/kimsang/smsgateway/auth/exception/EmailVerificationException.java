package com.kimsang.smsgateway.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class EmailVerificationException extends AuthenticationException {
  public EmailVerificationException(String message) {
    super(message);
  }
}
