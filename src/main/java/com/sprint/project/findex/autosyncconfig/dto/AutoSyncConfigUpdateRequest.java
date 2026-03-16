package com.sprint.project.findex.autosyncconfig.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AutoSyncConfigUpdateRequest {
  private boolean enabled;
}
