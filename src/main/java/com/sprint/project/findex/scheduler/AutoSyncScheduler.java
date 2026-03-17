package com.sprint.project.findex.scheduler;

import com.sprint.project.findex.service.AutoSyncConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoSyncScheduler {

   private final AutoSyncConfigService autoSyncConfigService;

  // application.yml에서 설정한 cron 값을 호출
  @Scheduled(cron = "${findex.scheduler.auto-sync.cron}")
  public void executeAutoSync() {
    log.info("[Batch Start] 자동 연동 배치 실행 시작");

    try {
       autoSyncConfigService.syncEnabledIndices();

      log.info("[Batch End] 자동 연동 배치 실행 완료");
    } catch (Exception e) {
      log.error("[Batch Error] 자동 연동 배치 실행 중 오류 발생: {}", e.getMessage());
    }
  }
}
