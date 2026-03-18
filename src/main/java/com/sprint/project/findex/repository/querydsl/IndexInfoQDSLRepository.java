package com.sprint.project.findex.repository.querydsl;

import com.sprint.project.findex.dto.indexinfo.IndexInfoCursorPageRequest;
import com.sprint.project.findex.dto.indexinfo.IndexInfoSummaryDto;
import com.sprint.project.findex.entity.IndexInfo;
import java.util.List;

public interface IndexInfoQDSLRepository {

  List<IndexInfoSummaryDto> findDistinctClassificationsAndNames();

  List<IndexInfo> findByCursor(IndexInfoCursorPageRequest request);

  Long getTotalElements(IndexInfoCursorPageRequest request);
}
