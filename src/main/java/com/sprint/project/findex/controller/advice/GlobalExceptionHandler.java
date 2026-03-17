package com.sprint.project.findex.controller.advice;

import com.sprint.project.findex.dto.ErrorResponse;
import com.sprint.project.findex.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(
      IllegalArgumentException e
  ) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            new ErrorResponse(
                Instant.now(),
                400,
                "잘못된 요청입니다.",
                e.getMessage()
            )
        );
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(
      MethodArgumentTypeMismatchException e
  ) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(
            Instant.now(),
            400,
            "잘못된 요청입니다.",
            e.getMessage()
        ));
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(
      ResourceNotFoundException e
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            new ErrorResponse(
                Instant.now(),
                404,
                "데이터가 존재하지 않습니다.",
                e.getMessage()
            )
        );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(
      Exception e
  ) {
    log.error("Unhandled exception", e);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            new ErrorResponse(
                Instant.now(),
                500,
                "내부 서버 오류입니다.",
                "예상치 못한 오류가 발생했습니다."
            )
        );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      MethodArgumentNotValidException e
  ) {
    String details = e.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .findFirst()
        .orElse("입력값이 올바르지 않습니다.");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(
            Instant.now(),
            400,
            "잘못된 요청입니다.",
            details
        ));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadable(
      HttpMessageNotReadableException e
  ) {
    return ResponseEntity.badRequest()
        .body(new ErrorResponse(
            Instant.now(),
            400,
            "잘못된 요청입니다.",
            "요청 본문을 올바르게 입력해주세요."
        ));
  }
}

