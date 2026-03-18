package com.sprint.project.findex.repository;

import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.entity.SyncJob;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SyncJobRepository extends JpaRepository<SyncJob, Long>, SyncJobQDSLRepository {

  @Query("SELECT s.indexInfo.id AS indexInfoId, MAX(s.targetDate) AS lastDate " +
      "FROM SyncJob s " +
      "WHERE s.indexInfo IN :indexInfos AND s.result = 'SUCCESS' " +
      "GROUP BY s.indexInfo.id")
  List<LastSyncDateProjection> findLastSyncDates(@Param("indexInfos") Set<IndexInfo> indexInfos);

  interface LastSyncDateProjection {
    Long getIndexInfoId();
    LocalDate getLastDate();
  }

  @Query("SELECT s.indexInfo.id AS indexInfoId, MAX(s.targetDate) AS lastDate " +
      "FROM SyncJob s " +
      "JOIN AutoSyncConfig asc ON s.indexInfo = asc.indexInfo " +
      "WHERE asc.enabled = true AND s.result = 'SUCCESS' " +
      "GROUP BY s.indexInfo.id")
  List<LastSyncDateProjection> findLastSyncDatesEnabledOnly();
}
