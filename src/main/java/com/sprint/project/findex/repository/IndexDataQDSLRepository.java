package com.sprint.project.findex.repository;

import com.sprint.project.findex.dto.indexdata.CursorPageIndexDataRequest;
import com.sprint.project.findex.dto.indexdata.IndexDataCsvExportRequest;
import com.sprint.project.findex.entity.IndexData;
import java.util.List;
import org.springframework.data.domain.Slice;

public interface IndexDataQDSLRepository {
  Slice<IndexData> findCursorPage(CursorPageIndexDataRequest request);

  List<IndexData> findAllForExport(IndexDataCsvExportRequest request);

  Long countByRequest(CursorPageIndexDataRequest request);
}
