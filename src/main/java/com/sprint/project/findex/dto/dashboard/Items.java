package com.sprint.project.findex.dto.dashboard;

import java.time.LocalDate;

public record Items(
    LocalDate date,
    Double value
) { }
