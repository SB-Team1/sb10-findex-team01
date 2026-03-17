package com.sprint.project.findex.repository;

import com.sprint.project.findex.dto.syncjob.SyncJobRequestQuery;
import com.sprint.project.findex.entity.SyncJob;
import java.util.List;

public interface SyncJobQDSLRepository {

  List<SyncJob> search(SyncJobRequestQuery condition);

  long countWithFilter(SyncJobRequestQuery condition);
}
