package com.sprint.project.findex.dto.autosyncconfig;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AutoSyncConfigListResponse {

  private List<AutoSyncConfigDto> content;

  private String nextCursor;

  private Long nextIdAfter;

  private Integer size;

  private Long totalElements;

  private boolean hasNext;
}
