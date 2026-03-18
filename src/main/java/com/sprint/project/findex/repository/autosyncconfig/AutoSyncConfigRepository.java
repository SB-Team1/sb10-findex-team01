package com.sprint.project.findex.repository.autosyncconfig;

import com.sprint.project.findex.entity.AutoSyncConfig;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig, Long>, AutoSyncConfigRepositoryCustom {

  @EntityGraph(attributePaths = "indexInfo")
  List<AutoSyncConfig> findByEnabledTrue();
}
