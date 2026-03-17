package com.sprint.project.findex.mapper;

import com.sprint.project.findex.dto.dashboard.DashboardQueryDto;
import com.sprint.project.findex.dto.dashboard.IndexPerformanceDto;

public final class DashboardMapper {

  private DashboardMapper() {
    throw new AssertionError("유틸 클래스는 인스턴스화할 수 없습니다.");
  }

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
