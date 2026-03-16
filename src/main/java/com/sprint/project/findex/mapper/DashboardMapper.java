package com.sprint.project.findex.mapper;

import com.sprint.project.findex.dto.dashboard.DashboardQueryDto;
import com.sprint.project.findex.dto.dashboard.IndexPerformanceDto;

public class DashboardMapper {
  public static IndexPerformanceDto toIndexPerformanceDto(DashboardQueryDto dto) {
    double currentPrice = dto.currentPrice();
    double beforePrice = dto.beforePrice();
    double versus = currentPrice - beforePrice;
    double fluctuationRate = versus * 100.0 / beforePrice;

    return new IndexPerformanceDto(
        dto.indexInfoId(),
        dto.indexClassification(),
        dto.indexName(),
        versus,
        fluctuationRate,
        currentPrice,
        beforePrice
    );
  }
}
