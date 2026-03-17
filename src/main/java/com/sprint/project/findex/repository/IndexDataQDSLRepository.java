package com.sprint.project.findex.repository;

import com.sprint.project.findex.dto.indexdata.CursorPageIndexDataRequest;
import com.sprint.project.findex.entity.IndexData;
import org.springframework.data.domain.Slice;

public interface IndexDataQDSLRepository {
  Slice<IndexData> findCursorPage(CursorPageIndexDataRequest request);

  Long countByRequest(CursorPageIndexDataRequest request);
}
