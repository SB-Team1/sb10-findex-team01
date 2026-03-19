package com.sprint.project.findex.dto.dashboard;

import java.util.List;

public record IndexChartDto(
    Long indexInfoId,
    String indexClassification,
    String indexName,
    String periodType,
    List<Items> dataPoints,
    List<Items> ma5DataPoints,
    List<Items> ma20DataPoints
) { }
