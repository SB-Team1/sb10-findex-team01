package com.sprint.project.findex.service;

import com.sprint.project.findex.dto.dashboard.DashboardQueryDto;
import com.sprint.project.findex.dto.dashboard.IndexChartDto;
import com.sprint.project.findex.dto.dashboard.Items;
import com.sprint.project.findex.dto.dashboard.RankedIndexPerformanceDto;
import com.sprint.project.findex.dto.dashboard.RankingRequest;
import com.sprint.project.findex.dto.dashboard.IndexPerformanceDto;
import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.global.exception.ApiException;
import com.sprint.project.findex.global.exception.ErrorCode;
import com.sprint.project.findex.mapper.DashboardMapper;
import com.sprint.project.findex.repository.DashboardRepository;
import com.sprint.project.findex.repository.IndexInfoRepository;
import com.sprint.project.findex.repository.projection.DashboardRankingProjection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DashboardService {

  private final DashboardRepository dashboardRepository;
  private final IndexInfoRepository indexInfoRepository;

  public List<IndexPerformanceDto> findFavoriteIndexPerformance(String periodType) {

    LocalDate targetDate = calculateTargetDate(LocalDate.now(), periodType);

    return dashboardRepository.findChangedFavoriteIndexPerformance(targetDate)
        .stream()
        .map(DashboardMapper::toIndexPerformanceDto)
        .toList();
  }

  public List<RankedIndexPerformanceDto> findIndexRanking(RankingRequest request) {

    LocalDate today = LocalDate.now();
    LocalDate compareDate = calculateTargetDate(today, request.periodType());

    // indexInfoId값이 null이면 지수 전체 조회
    List<DashboardRankingProjection> projection = (request.indexInfoId() == null)
        ? dashboardRepository.findAllIndexRanking(today, compareDate)
        : dashboardRepository.findIndexRankingByIndexInfoId(request.indexInfoId(), today, compareDate);

    List<DashboardQueryDto> queryResult = projection.stream()
        .map(DashboardMapper::toQueryDto)
        .toList();

    List<IndexPerformanceDto> sortedPerformances = queryResult.stream()
        .map(DashboardMapper::toIndexPerformanceDto)
        .sorted((a, b) -> Double.compare(b.fluctuationRate(), a.fluctuationRate()))
        .limit(request.limitOrDefault())
        .toList();

    List<RankedIndexPerformanceDto> rankedResult = new ArrayList<>();

    for (int i = 0; i < sortedPerformances.size(); i++) {
      rankedResult.add(new RankedIndexPerformanceDto(
          sortedPerformances.get(i),
          i + 1
      ));
    }

    return rankedResult;
  }

  public IndexChartDto findIndexChart(Long indexInfoId, String periodType) {

    IndexInfo index = indexInfoRepository.findById(indexInfoId)
        .orElseThrow(() -> new ApiException(ErrorCode.INDEX_INFO_ID_NOT_FOUND, indexInfoId));

    List<Object[]> rows = dashboardRepository.findIndexChartData(
        indexInfoId,
        periodType
    );

    List<Items> dataPoints = new ArrayList<>();
    List<Items> ma5DataPoints = new ArrayList<>();
    List<Items> ma20DataPoints = new ArrayList<>();

    for (Object[] row: rows) {

      LocalDate date = ((Date) row[0]).toLocalDate();
      Double closePrice = ((Number) row[1]).doubleValue();
      Double ma5 = row[2] != null
                ? ((Number) row[2]).doubleValue()
                : null;
      Double ma20 = row[3] != null
          ? ((Number) row[3]).doubleValue()
          : null;

      dataPoints.add(new Items(date, closePrice));

      if (ma5 != null) {
        ma5DataPoints.add(new Items(date, ma5));
      }

      if (ma20 != null) {
        ma20DataPoints.add(new Items(date, ma20));
      }
    }

    return new IndexChartDto(
        index.getId(),
        index.getIndexClassification(),
        index.getIndexName(),
        periodType,
        dataPoints,
        ma5DataPoints,
        ma20DataPoints
    );
  }

  private LocalDate calculateTargetDate(LocalDate currentDate, String periodType) {

    return switch (periodType) {
      case "DAILY" -> currentDate.minusDays(1);
      case "WEEKLY" -> currentDate.minusWeeks(1);
      case "MONTHLY" -> currentDate.minusMonths(1);
      default -> throw new ApiException(ErrorCode.INVALID_PERIOD_TYPE, periodType);
    };
  }
}
