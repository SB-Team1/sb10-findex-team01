package com.sprint.project.findex.controller;

import com.sprint.project.findex.dto.syncjob.CursorPageResponseSyncJobDto;
import com.sprint.project.findex.dto.syncjob.IndexDataSyncRequest;
import com.sprint.project.findex.dto.syncjob.SyncJobDto;
import com.sprint.project.findex.dto.syncjob.SyncJobRequestQuery;
import com.sprint.project.findex.service.SyncJobService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sync-jobs")
@RequiredArgsConstructor
public class SyncJobController {

  private final SyncJobService syncJobService;

  @PostMapping("/index-infos")
  public ResponseEntity<List<SyncJobDto>> syndIndexInfo(HttpServletRequest request) {
    return ResponseEntity.ok(syncJobService.syncIndexInfos(request));
  }

  @PostMapping("/index-data")
  public ResponseEntity<?> syncIndexData(
      @Valid @RequestBody IndexDataSyncRequest indexDataSyncRequest,
      HttpServletRequest request) {
    return ResponseEntity.ok(syncJobService.syncIndexData(indexDataSyncRequest, request));
  }

  @GetMapping
  public ResponseEntity<CursorPageResponseSyncJobDto> findSyncJobs(
      @ParameterObject @ModelAttribute SyncJobRequestQuery query) {
    return ResponseEntity.ok(syncJobService.findSyncJobs(query));
  }

}
