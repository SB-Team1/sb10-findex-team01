package com.sprint.project.findex.dto.dashboard;

public record DashboardQueryDto(

    Long indexInfoId,
    String indexClassification,
    String indexName,
    Double currentPrice,
    Double beforePrice

) { }
