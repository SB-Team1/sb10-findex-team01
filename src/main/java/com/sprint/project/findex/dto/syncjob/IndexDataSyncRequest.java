package com.sprint.project.findex.dto.syncjob;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record IndexDataSyncRequest(
    @NotNull
    List<Long> indexInfoIds,

    @NotNull
    LocalDate baseDateFrom,

    @NotNull
    LocalDate baseDateTo

) {

  @AssertTrue(message = "종료일은 시작일과 같거나 그 이후여야 합니다.")
  public boolean isValidDateRange() {
    return !baseDateFrom.isAfter(baseDateTo);
  }
}
