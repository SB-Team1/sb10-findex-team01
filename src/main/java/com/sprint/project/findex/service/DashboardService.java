package com.sprint.project.findex.service;

import com.sprint.project.findex.dto.dashboard.DashboardQueryDto;
import com.sprint.project.findex.dto.dashboard.RankedIndexPerformanceDto;
import com.sprint.project.findex.dto.dashboard.RankingRequest;
import com.sprint.project.findex.entity.DeletedStatus;
import com.sprint.project.findex.dto.dashboard.IndexPerformanceDto;
import com.sprint.project.findex.mapper.DashboardMapper;
import com.sprint.project.findex.repository.DashboardRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DashboardService {

  private final DashboardRepository dashboardRepository;

  public List<IndexPerformanceDto> findFavoriteIndexPerformance(String periodType) {

    LocalDate targetDate = calculateTargetDate(LocalDate.now(), periodType);

    return dashboardRepository.findChangedFavoriteIndexPerformance(targetDate, DeletedStatus.ACTIVE)
        .stream()
        .map(DashboardMapper::toIndexPerformanceDto)
        .toList();
  }

  public List<RankedIndexPerformanceDto> findIndexRanking(RankingRequest request) {

    LocalDate today = LocalDate.now();
    LocalDate compareDate = calculateTargetDate(today, request.periodType());

    // indexInfoId값이 null이면 지수 전체 조회
    List<DashboardQueryDto> queryResult = (request.indexInfoId() == null)
        ? dashboardRepository.findAllIndexRanking(today, compareDate, DeletedStatus.ACTIVE)
        : dashboardRepository.findIndexRankingByIndexInfoId(request.indexInfoId(), today, compareDate, DeletedStatus.ACTIVE);

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

  private LocalDate calculateTargetDate(LocalDate currentDate, String periodType) {

    return switch (periodType) {
      case "DAILY" -> currentDate.minusDays(1);
      case "WEEKLY" -> currentDate.minusWeeks(1);
      case "MONTHLY" -> currentDate.minusMonths(1);
      default -> throw new IllegalArgumentException("잘못된 periodType 입니다.");
    };
  }
}
