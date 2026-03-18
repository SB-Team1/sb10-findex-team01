package com.sprint.project.findex.dto.indexinfo;

import java.util.List;
import lombok.Builder;

@Builder
public record CursorPageResponseIndexInfoDto(List<IndexInfoDto> content, String nextCursor,
                                             Long nextIdAfter,
                                             int size, Long totalElements,
                                             boolean hasNext) {

}
