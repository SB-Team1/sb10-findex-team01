package com.sprint.project.findex.dto.indexdata;

import com.sprint.project.findex.dto.SortDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

public record CursorPageIndexDataRequest(
    @Schema(description = "지수 정보 ID", example = "1")
    Long indexInfoId,
    @Schema(description = "시작 일자", example = "2023-01-01")
    LocalDate startDate,
    @Schema(description = "종료 일자", example = "2023-01-01")
    LocalDate endDate,
    @Min(1)
    @Schema(description = "이전 페이지 마지막 요소 ID")
    Long idAfter,
    @Schema(description = "커서 (다음 페이지 시작점)")
    String cursor,
    @Schema(description = "정렬 필드 (baseDate, marketPrice, closingPrice, highPrice, lowPrice, versus, fluctuationRate, tradingQuantity, tradingPrice, marketTotalAmount)", example = "baseDate")
    IndexDataSortField sortField,
    @Schema(description = "정렬 방향 (asc, desc)", example = "desc")
    SortDirection sortDirection,
    @Min(1)
    @Max(500)
    @Schema(description = "페이지 크기", example = "10")
    Integer size
) {

  public CursorPageIndexDataRequest {
    if (size == null) {
      size = 10;
    }

    if (sortField == null) {
      sortField = IndexDataSortField.BASE_DATE;
    }

    if (sortDirection == null) {
      sortDirection = SortDirection.DESC;
    }
  }
}
