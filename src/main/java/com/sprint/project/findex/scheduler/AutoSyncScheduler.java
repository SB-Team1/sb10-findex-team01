package com.sprint.project.findex.scheduler;

import com.sprint.project.findex.entity.AutoSyncConfig;
import com.sprint.project.findex.repository.autosyncconfig.AutoSyncConfigRepository;
import com.sprint.project.findex.service.AutoSyncService;
import com.sprint.project.findex.service.openapi.OpenApiService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoSyncScheduler {

  private final AutoSyncService autoSyncService;

  // application.yml에서 설정한 cron 값을 호출
  @Scheduled(cron = "${findex.scheduler.auto-sync.cron}")
  public void executeAutoSync() {
    log.info("[Batch Start] 자동 연동 배치 실행 시작");

    autoSyncService.syncEnabledIndices();

    log.info("[Batch End] 자동 연동 배치 실행 완료");
  }
}
