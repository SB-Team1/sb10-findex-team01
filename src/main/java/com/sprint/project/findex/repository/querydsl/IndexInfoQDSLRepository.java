package com.sprint.project.findex.repository.querydsl;

import com.sprint.project.findex.dto.indexinfo.IndexInfoCursorPageRequest;
import com.sprint.project.findex.entity.IndexInfo;
import java.util.List;

public interface IndexInfoQDSLRepository {

  List<IndexInfo> findByCursor(IndexInfoCursorPageRequest request);
}
