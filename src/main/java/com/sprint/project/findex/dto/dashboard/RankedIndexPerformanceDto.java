package com.sprint.project.findex.dto.dashboard;

public record RankedIndexPerformanceDto(
    IndexPerformanceDto performance,
    Integer rank
) { }
