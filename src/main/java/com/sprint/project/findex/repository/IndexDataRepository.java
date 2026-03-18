package com.sprint.project.findex.repository;

import com.sprint.project.findex.entity.DeletedStatus;
import com.sprint.project.findex.entity.IndexData;
import com.sprint.project.findex.entity.IndexInfo;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IndexDataRepository extends JpaRepository<IndexData, Long>,
    IndexDataQDSLRepository {
  boolean existsByIndexInfoAndBaseDate(IndexInfo indexInfo, LocalDate baseDate);



  @EntityGraph(attributePaths = "indexInfo")
  Optional<IndexData> findTopByIndexInfoOrderByBaseDateDesc(IndexInfo indexInfo);

  @Query("SELECT i.indexInfo.id AS indexInfoId, MAX(i.baseDate) AS lastDate " +
      "FROM IndexData i " +
      "WHERE i.indexInfo IN :indexInfos " +
      "GROUP BY i.indexInfo.id")
  List<LastSyncDateProjection> findLastSyncDates(@Param("indexInfos") List<IndexInfo> indexInfos);

  interface LastSyncDateProjection {

    Long getIndexInfoId();

    LocalDate getLastDate();
  }

  List<IndexData> findByIndexInfoAndBaseDateBetweenAnd(IndexInfo indexInfo,
      LocalDate fromDate,
      LocalDate toDate);
}
