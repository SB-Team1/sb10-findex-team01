package com.sprint.project.findex.repository;

import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.repository.querydsl.IndexInfoQDSLRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long>,
    IndexInfoQDSLRepository {

}
