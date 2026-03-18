package com.sprint.project.findex.service;


import com.sprint.project.findex.entity.AutoSyncConfig;
import com.sprint.project.findex.global.exception.ApiException;
import com.sprint.project.findex.service.openapi.OpenApiService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoSyncService {

  private final AutoSyncConfigService autoSyncConfigService;
  private final OpenApiService openApiService;
  /**
   * 스케줄러에 의해 호출될 메인 로직
   */
  public void syncEnabledIndices() {
    log.info("[AutoSync] 자동 연동 활성화 지수 조회 시작");

    // 1. 설정 서비스를 통해 대상 목록만 가져옴
    List<AutoSyncConfig> enabledConfigs = autoSyncConfigService.getEnabledConfigs();

    if (enabledConfigs.isEmpty()) {
      log.info("[AutoSync] 활성화 된 자동 연동 지수 없음, 배치를 종료합니다.");
      return;
    }

    log.info("[AutoSync] 조회된 자동 연동 대상 개수: {}", enabledConfigs.size());

    try {
      openApiService.fetchAndSaveByAutoSync(enabledConfigs);
    } catch (ApiException e) {
      // 2. 이미 OpenApiService에서 ApiException을 던진 경우
      log.error("[AutoSync] API 연동 중 커스텀 에러 발생: {}", e.getErrorCode().getMessage(), e);
    } catch (Exception e) {
      // 그 외 예상치 못한 시스템 에러 격리 (스케줄러가 뻗지 않도록 throw 하지 않음)
      log.error("[AutoSync] 자동 연동 처리 중 치명적 오류 발생", e);
    }
  }
}
