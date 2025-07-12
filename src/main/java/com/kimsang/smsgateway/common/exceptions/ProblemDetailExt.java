package com.kimsang.smsgateway.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.Map;

public class ProblemDetailExt {
  public static ProblemDetail forStatusDetailAndErrors(HttpStatus status, String detail, Map<String, String> errors) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
    problemDetail.setProperty("errors", errors);
    return problemDetail;
  }
}
