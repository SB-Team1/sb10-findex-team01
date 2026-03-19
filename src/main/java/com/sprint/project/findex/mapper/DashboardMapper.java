package com.sprint.project.findex.mapper;

import com.sprint.project.findex.dto.dashboard.DashboardQueryDto;
import com.sprint.project.findex.dto.dashboard.IndexPerformanceDto;
import com.sprint.project.findex.repository.dashboard.projection.DashboardRankingProjection;
import java.text.DecimalFormat;

public final class DashboardMapper {

  private DashboardMapper() {
    throw new AssertionError("유틸 클래스는 인스턴스화할 수 없습니다.");
  }

  public static IndexPerformanceDto toIndexPerformanceDto(DashboardQueryDto dto) {
    double currentPrice = dto.currentPrice();
    double beforePrice = dto.beforePrice();
    double versus = currentPrice - beforePrice;
    double fluctuationRate = versus * 100.0 / beforePrice;

    double normalizedFR = Math.round(fluctuationRate * 100) / 100.0;

    return new IndexPerformanceDto(
        dto.indexInfoId(),
        dto.indexClassification(),
        dto.indexName(),
        versus,
        normalizedFR,
        currentPrice,
        beforePrice
    );
  }

  public static DashboardQueryDto toQueryDto(DashboardRankingProjection projection) {
    return new DashboardQueryDto(
        projection.getId(),
        projection.getIndexClassification(),
        projection.getIndexName(),
        projection.getCurrentClosingPrice(),
        projection.getBeforeClosingPrice()
    );
  }
}
