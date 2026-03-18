package com.sprint.project.findex.dto.syncjob;

import com.sprint.project.findex.global.entity.JobType;
import com.sprint.project.findex.global.entity.ResultType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record SyncJobRequestQuery(
    // 검색 필터
    JobType jobType,
    Long indexInfoId,
    LocalDate baseDateFrom,
    LocalDate baseDateTo,
    String worker,
    LocalDateTime jobTimeFrom,
    LocalDateTime jobTimeTo,
    ResultType status, // SUCCESS, FAILED

    // 페이징 및 정렬
    Long idAfter, // 이전 페이지 마지막 요소 ID
    String cursor, // 커서
    String sortField, // targetDate, jobTime
    String sortDirection, // asc, desc
    Integer size
) {

  // 기본값 설정
  public SyncJobRequestQuery {
    if (size == null) {
      size = 10;
    }

    if (sortField == null) {
      sortField = "jobTime";
    }

    if (sortDirection == null) {
      sortDirection = "desc";
    }
  }
}
