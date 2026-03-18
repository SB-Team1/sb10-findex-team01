package com.sprint.project.findex.controller;

import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigDto;
import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigListRequest;
import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigListResponse;
import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigUpdateRequest;
import com.sprint.project.findex.dto.ErrorResponse;
import com.sprint.project.findex.service.AutoSyncConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "자동 연동 설정 API", description = "자동 연동 설정 관리 API")
@RestController
@RequestMapping("/api/auto-sync-configs")
@RequiredArgsConstructor
public class AutoSyncConfigController {

  private final AutoSyncConfigService autoSyncConfigService;

  @PatchMapping("/{id}")
  @Operation(summary = "자동 연동 설정 수정")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "자동 연동 설정 수정 성공",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AutoSyncConfigUpdateRequest.class)
          )
      ),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 설정 값 등)",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(responseCode = "404", description = "수정할 자동 연동 설정을 찾을 수 없음",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(responseCode = "500", description = "서버 오류",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  public ResponseEntity<AutoSyncConfigDto> update(@PathVariable Long id,
                                  @RequestBody AutoSyncConfigUpdateRequest request){
    AutoSyncConfigDto autoSyncConfigDto = autoSyncConfigService.update(id, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(autoSyncConfigDto);

  }



  @GetMapping
  @Operation(summary = "자동 연동 설정 목록 조회", description = "자동 연동 설정 목록을 조회합니다. 필터링, 정렬, 커서 기반 페이지네이션을 지원합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "자동 연동 설정 목록 조회 성공",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AutoSyncConfigListResponse.class)
          )
      ),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      ),
      @ApiResponse(responseCode = "500", description = "서버 오류",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)
          )
      )
  })
  public ResponseEntity<AutoSyncConfigListResponse> getList(
      @ParameterObject @ModelAttribute AutoSyncConfigListRequest condition
  ) {
    AutoSyncConfigListResponse response = autoSyncConfigService.getList(condition);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }
}
