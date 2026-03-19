package com.sprint.project.findex.dto.syncjob;

import java.util.List;
import lombok.Builder;

@Builder
public record CursorPageResponseSyncJobDto(
    List<SyncJobDto> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    Long totalElements,
    boolean hasNext
) {

}
