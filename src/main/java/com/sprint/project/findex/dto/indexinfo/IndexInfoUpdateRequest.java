package com.sprint.project.findex.dto.indexinfo;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record IndexInfoUpdateRequest(@NotNull Integer employedItemsCount,
                                     @NotNull LocalDate basePointInTime,
                                     @NotNull Double baseIndex,
                                     @NotNull boolean favorite) {

}
