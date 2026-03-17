package com.sprint.project.findex.dto.indexinfo;

import com.sprint.project.findex.entity.SourceType;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record IndexInfoDto(Long id, String indexClassification, String indexName,
                           Integer employedItemsCount, LocalDate basePointInTime, Double baseIndex,
                           SourceType sourceType, boolean favorite) {

}
