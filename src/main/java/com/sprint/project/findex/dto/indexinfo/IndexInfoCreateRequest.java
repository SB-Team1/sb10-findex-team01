package com.sprint.project.findex.dto.indexinfo;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record IndexInfoCreateRequest(@NotNull String indexClassification,
                                     @NotNull String indexName,
                                     @NotNull Integer employedItemsCount,
                                     @NotNull LocalDate basePointInTime,
                                     @NotNull Double baseIndex,
                                     @NotNull boolean favorite) {

}
