package com.sprint.project.findex.dto.indexdata;

import com.sprint.project.findex.dto.SortDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record IndexDataCsvExportRequest(
    @Schema(description = "지수 정보 ID", example = "1")
    Long indexInfoId,
    @Schema(description = "시작 일자", example = "2023-01-01")
    LocalDate startDate,
    @Schema(description = "종료 일자", example = "2023-01-01")
    LocalDate endDate,
    @Schema(description = "정렬 필드 (baseDate, marketPrice, closingPrice, highPrice, lowPrice, versus, fluctuationRate, tradingQuantity, tradingPrice, marketTotalAmount)", example = "baseDate")
    IndexDataSortField sortField,
    @Schema(description = "정렬 방향 (asc, desc)", example = "desc")
    SortDirection sortDirection
) {

  public IndexDataCsvExportRequest {
    if (sortField == null) {
      sortField = IndexDataSortField.BASE_DATE;
    }
    if (sortDirection == null) {
      sortDirection = SortDirection.DESC;
    }
  }

}
