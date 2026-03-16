package com.sprint.project.findex.controller;

import com.sprint.project.findex.dto.IndexDataCreateRequest;
import com.sprint.project.findex.dto.IndexDataDto;
import com.sprint.project.findex.dto.dashboard.IndexPerformanceDto;
import com.sprint.project.findex.service.DashboardService;
import com.sprint.project.findex.service.IndexDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/index-data")
@Tag(name = "지수 데이터 API")
public class IndexDataController {

  private final IndexDataService indexDataService;
  private final DashboardService dashboardService;

  @PostMapping
  @Operation(summary = "지수 데이터 등록")
  public ResponseEntity<IndexDataDto> create(
      @Valid @RequestBody IndexDataCreateRequest request) {
    IndexDataDto indexDataDto = indexDataService.createByUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(indexDataDto);
  }

  @GetMapping(value = "/performance/favorite")
  public ResponseEntity<List<IndexPerformanceDto>> getIndexPerformance(
      @RequestParam("periodType") String periodType
  ) {
    List<IndexPerformanceDto> dto = dashboardService.findFavoriteIndexPerformance(periodType);

    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }
}
