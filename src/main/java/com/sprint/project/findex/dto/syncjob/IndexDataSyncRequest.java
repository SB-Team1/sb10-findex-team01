package com.sprint.project.findex.dto.syncjob;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record IndexDataSyncRequest(
    @NotNull
    List<Long> indexInfoIds,

    @NotNull
    LocalDate baseDateFrom,

    @NotNull
    LocalDate baseDateTo
) {

}
