package com.sprint.project.findex.dto.autosyncconfig;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AutoSyncConfigListRequest {
  private Long indexInfoId;
  private Boolean enabled;
  private Long idAfter;
  private String cursor;
  private String sortField = "indexInfo.indexName";
  private String sortDirection = "asc";
  private Integer size = 10;
}
