package com.sprint.project.findex.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // =========================
  // 500 INTERNAL SERVER ERROR
  // =========================
  INTERNAL_SERVER_ERROR(500, "내부 서버 오류입니다."),
  FILE_CANT_WRITE(500, "파일 저장에 실패했습니다."),
  EXTERNAL_API_ERROR(500, "외부 API 호출에 실패했습니다."),
  DATABASE_ERROR(500, "데이터베이스 처리 중 오류가 발생했습니다."),
  OPEN_API_REQUEST_FAILED(500, "Open API 데이터 요청에 실패하였습니다.(%s)"),
  OPEN_API_INVALID_RESPONSE(500, "Open API의 응답 형식이 잘못되었습니다."),
  VALID_BASE_DATE_NOT_FOUND(500, "지수 정보를 얻을 기준일자를 구하지 못했습니다."),
  FILE_EXPORT_FAILED(500, "파일 추출 및 다운로드 처리 중 서버 오류가 발생했습니다."),

  // =========================
  // 400 BAD REQUEST
  // =========================
  INVALID_REQUEST(400, "잘못된 요청입니다."),
  INVALID_PARAMETER(400, "요청 파라미터가 올바르지 않습니다."),
  INVALID_DATE_FORMAT(400, "날짜 형식이 올바르지 않습니다."),
  INVALID_PERIOD_TYPE(400, "지원하지 않는 기간 타입입니다."),
  INVALID_LIMIT(400, "limit 값이 올바르지 않습니다."),
  REQUEST_BODY_MISSING(400, "요청 본문이 올바르지 않습니다."),

  // =========================
  // 404 NOT FOUND
  // =========================
  RESOURCE_NOT_FOUND(404, "요청한 리소스를 찾을 수 없습니다."),
  INDEX_INFO_ID_NOT_FOUND(404, "해당 지수 정보 id를 찾을 수 없습니다."),
  INDEX_DATA_NOT_FOUND(404, "해당 지수 데이터가 존재하지 않습니다."),

  // =========================
  // 409 CONFLICT
  // =========================
  INDEX_INFO_ALREADY_EXISTS(409, "이미 존재하는 지수 정보입니다."),
  INDEX_DATA_ALREADY_EXISTS(409, "이미 존재하는 지수 데이터입니다."),
  ;

  private final int status;
  private final String message;
}
