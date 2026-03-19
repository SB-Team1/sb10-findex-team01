package com.sprint.project.findex.repository.indexinfo;

import com.sprint.project.findex.entity.IndexInfo;
import java.util.List;
import com.sprint.project.findex.repository.indexinfo.querydsl.IndexInfoQDSLRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long>,
    IndexInfoQDSLRepository {

  List<IndexInfo> findByIdIn(List<Long> ids);
  Optional<IndexInfo> findByIndexClassificationAndIndexName(String indexClassification,
      String indexName);

  @Query("SELECT i FROM IndexInfo i WHERE CONCAT(i.indexClassification, '_', i.indexName) IN :keys")
  List<IndexInfo> findByKeyIn(@Param("keys") List<String> keys);
}
