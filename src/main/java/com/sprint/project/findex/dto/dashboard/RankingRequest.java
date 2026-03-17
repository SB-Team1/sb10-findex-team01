package com.sprint.project.findex.dto.dashboard;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RankingRequest(
    Long indexInfoId,
    @NotBlank(message = "periodType은 필수입니다.")
    String periodType,
    @Min(value = 1, message = "limit은 1 이상이어야 합니다.")
    Integer limit
) {
  public int limitOrDefault() {
    return limit == null ? 10 : limit;
  }
}
