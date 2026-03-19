package com.sprint.project.findex.global.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
  private final ErrorCode errorCode;
  private final String detail;

  public ApiException(ErrorCode errorCode, Object source) {
    this.errorCode = errorCode;
    this.detail = source.toString();
  }

  public ApiException(ErrorCode errorCode) {
    this(errorCode, "");
  }
}
