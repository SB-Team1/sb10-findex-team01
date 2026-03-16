package com.sprint.project.findex.dto.autosyncconfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoSyncConfigDto {

  private Integer id;
  private Integer indexInfoId;
  private String indexClassification;
  private String indexName;
  private boolean enabled = false;
}
