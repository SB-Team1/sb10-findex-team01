package com.sprint.project.findex.controller.advice;

import com.sprint.project.findex.global.exception.ApiException;
import com.sprint.project.findex.global.exception.ErrorCode;
import com.sprint.project.findex.global.exception.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiErrorResponse> handleApiException(
      ApiException e,
      HttpServletRequest request
  ) {
    ErrorCode errorCode = e.getErrorCode();
    return ResponseEntity.status(errorCode.getStatus())
        .body(
            new ApiErrorResponse(
                Instant.now(),
                errorCode.getStatus(),
                errorCode.getMessage(),
                e.getDetail(),
                request.getRequestURI()
            )
        );
  }
}
