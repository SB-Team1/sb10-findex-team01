package com.sprint.project.findex.dto.indexinfo;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.sprint.project.findex.dto.SortDirection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class IndexInfoCursorPageRequest {

  private String indexClassification;
  private String indexName;
  private String cursor;
  private Boolean favorite;

  @Min(1)
  @JsonSetter(nulls = Nulls.SKIP)
  private Long idAfter = 1L;

  @JsonSetter(nulls = Nulls.SKIP)
  private IndexInfoSortField sortField = IndexInfoSortField.INDEX_CLASSIFICATION;

  @JsonSetter(nulls = Nulls.SKIP)
  private SortDirection sortDirection = SortDirection.ASC;

  @Min(1)
  @Max(500)
  @JsonSetter(nulls = Nulls.SKIP)
  private Integer size = 10;

}
