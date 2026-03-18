package com.sprint.project.findex.controller;

import com.sprint.project.findex.dto.indexinfo.CursorPageResponseIndexInfoDto;
import com.sprint.project.findex.dto.indexinfo.IndexInfoCreateRequest;
import com.sprint.project.findex.dto.indexinfo.IndexInfoCursorPageRequest;
import com.sprint.project.findex.dto.indexinfo.IndexInfoDto;
import com.sprint.project.findex.dto.indexinfo.IndexInfoSummaryDto;
import com.sprint.project.findex.dto.indexinfo.IndexInfoUpdateRequest;
import com.sprint.project.findex.service.IndexInfoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/index-infos")
public class IndexInfoController {

  private final IndexInfoService indexInfoService;

  @GetMapping
  public ResponseEntity<CursorPageResponseIndexInfoDto> findWithCursorPage(
      @ParameterObject @ModelAttribute @Valid IndexInfoCursorPageRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(indexInfoService.findWithCursorPage(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<IndexInfoDto> find(@PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK).body(indexInfoService.find(id));
  }

  @PostMapping
  public ResponseEntity<IndexInfoDto> create(@RequestBody @Valid IndexInfoCreateRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(indexInfoService.create(request));
  }

  @GetMapping("/summaries")
  public ResponseEntity<List<IndexInfoSummaryDto>> findSummaries() {
    return ResponseEntity.status(HttpStatus.OK).body(indexInfoService.findIndexInfoSummary());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    indexInfoService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PatchMapping("/{id}")
  public ResponseEntity<IndexInfoDto> update(@PathVariable Long id,
      @RequestBody @Valid IndexInfoUpdateRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(indexInfoService.update(id, request));
  }
}
