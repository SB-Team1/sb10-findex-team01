package com.sprint.project.findex.dto.indexdata;

import java.util.List;

public record CursorPageResponseIndexDataDto(
    List<IndexDataDto> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    Long totalElements,
    boolean hasNext
) {

}
