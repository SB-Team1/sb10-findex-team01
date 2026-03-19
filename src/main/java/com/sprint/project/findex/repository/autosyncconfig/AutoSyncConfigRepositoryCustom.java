package com.sprint.project.findex.repository.autosyncconfig;

import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigListRequest;
import com.sprint.project.findex.entity.AutoSyncConfig;
import java.util.List;

public interface AutoSyncConfigRepositoryCustom {
  List<AutoSyncConfig> findListByCursor(AutoSyncConfigListRequest condition);

  long countByCondition(AutoSyncConfigListRequest condition);
}
