package com.sprint.project.findex.dto.dashboard;

public record IndexPerformanceDto(

    Long indexInfoId,
    String indexClassification,
    String indexName,
    Double versus,
    Double fluctuationRate,
    Double currentPrice,
    Double beforePrice

) { }
