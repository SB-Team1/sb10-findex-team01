package com.sprint.project.findex.dto.indexinfo;

import com.sprint.project.findex.entity.IndexInfo;
import java.util.List;
import lombok.Builder;

@Builder
public record CursorPageResponseIndexInfoDto(List<IndexInfo> content, String nextCursor,
                                             Long nextIdAfter,
                                             int size, Long totalElements,
                                             boolean hasNext) {

}
