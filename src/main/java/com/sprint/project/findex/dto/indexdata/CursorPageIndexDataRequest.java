package com.sprint.project.findex.dto.indexdata;

import com.sprint.project.findex.dto.SortDirection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

public record CursorPageIndexDataRequest(
    Long indexInfoId,
    LocalDate startTime,
    LocalDate endDate,
    @Min(1)
    Long idAfter,
    String cursor,
    IndexDataSortField sortField,
    SortDirection sortDirection,
    @Min(1)
    @Max(500)
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
