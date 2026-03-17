package com.sprint.project.findex.controller;

import com.sprint.project.findex.dto.indexdata.CursorPageIndexDataRequest;
import com.sprint.project.findex.dto.indexdata.CursorPageResponseIndexDataDto;
import com.sprint.project.findex.dto.indexdata.IndexDataCreateRequest;
import com.sprint.project.findex.dto.indexdata.IndexDataDto;
import com.sprint.project.findex.dto.indexdata.IndexDataUpdateRequest;
import com.sprint.project.findex.dto.dashboard.IndexPerformanceDto;
import com.sprint.project.findex.dto.dashboard.RankedIndexPerformanceDto;
import com.sprint.project.findex.dto.dashboard.RankingRequest;
import com.sprint.project.findex.service.DashboardService;
import com.sprint.project.findex.service.IndexDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/index-data")
@Tag(name = "지수 데이터 API")
@Validated
public class IndexDataController {

  private final IndexDataService indexDataService;
  private final DashboardService dashboardService;

  @PostMapping
  @Operation(summary = "지수 데이터 등록")
  public ResponseEntity<IndexDataDto> create(
      @Valid @RequestBody IndexDataCreateRequest request
  ) {
    IndexDataDto dto = indexDataService.createByUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  @PatchMapping("/{id}")
  @Operation(summary = "지수 데이터 수정")
  public ResponseEntity<IndexDataDto> update(
      @PathVariable Long id,
      @Valid @RequestBody IndexDataUpdateRequest request
  ) {
    IndexDataDto dto = indexDataService.update(id, request);
    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "지수 데이터 삭제")
  public ResponseEntity<?> delete(
      @PathVariable Long id
  ) {
    indexDataService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Operation(summary = "지수 데이터 목록 조회")
  public ResponseEntity<CursorPageResponseIndexDataDto> findAll(
      @Valid @ModelAttribute CursorPageIndexDataRequest request
  ) {
    CursorPageResponseIndexDataDto dto = indexDataService.findAll(request);
    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  @GetMapping(value = "/performance/favorite")
  @Operation(summary = "주요 지수 현황 조회")
  public ResponseEntity<List<IndexPerformanceDto>> getIndexPerformance (
        @RequestParam("periodType") String periodType
  ){
      List<IndexPerformanceDto> dto = dashboardService.findFavoriteIndexPerformance(periodType);

      return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  @GetMapping(value = "/performance/rank")
  @Operation(summary = "지수 성과 랭킹 조회")
  public ResponseEntity<List<RankedIndexPerformanceDto>> getIndexRanking (
      @Valid @ModelAttribute RankingRequest request
  ) {
    List<RankedIndexPerformanceDto> dtos = dashboardService.findIndexRanking(request);

    return ResponseEntity.status(HttpStatus.OK).body(dtos);
  }
}
