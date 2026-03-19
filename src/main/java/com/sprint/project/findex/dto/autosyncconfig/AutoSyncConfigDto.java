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

  private Long id;
  private Long indexInfoId;
  private String indexClassification;
  private String indexName;

  @Builder.Default
  private boolean enabled = false;
}
