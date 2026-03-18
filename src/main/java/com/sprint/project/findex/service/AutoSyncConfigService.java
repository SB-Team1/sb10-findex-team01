package com.sprint.project.findex.service;

import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigDto;
import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigListRequest;
import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigListResponse;
import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigUpdateRequest;
import com.sprint.project.findex.entity.AutoSyncConfig;
import com.sprint.project.findex.mapper.AutoSyncConfigMapper;
import com.sprint.project.findex.repository.autosyncconfig.AutoSyncConfigRepository;
import com.sprint.project.findex.entity.IndexInfo;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoSyncConfigService {

  private final AutoSyncConfigRepository autoSyncConfigRepository;
  private final AutoSyncConfigMapper autoSyncConfigMapper; // MapStruct 인터페이스 주입

  @Transactional
  public AutoSyncConfigDto create(IndexInfo indexInfo) { // 변수명 indexInfoId -> indexInfo로 수정 (객체를 받으므로)
    if (indexInfo == null) {
      throw new IllegalArgumentException("지수 정보가 존재하지 않습니다.");
    }

    AutoSyncConfig autoSyncConfig = new AutoSyncConfig(null, indexInfo);
    AutoSyncConfig savedConfig = autoSyncConfigRepository.save(autoSyncConfig);

    return autoSyncConfigMapper.toDto(savedConfig);
  }

  @Transactional
  public AutoSyncConfigDto update(Integer id, AutoSyncConfigUpdateRequest request) {
    AutoSyncConfig autoSyncConfig = getAutoSyncConfig(id);
    autoSyncConfig.updateEnabled(request.isEnabled());

    return autoSyncConfigMapper.toDto(autoSyncConfig);
  }

  private AutoSyncConfig getAutoSyncConfig(Integer id) {
    return autoSyncConfigRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 자동 연동 설정입니다. ID: " + id));
  }

  // 스케줄러 로직
  // Todo: 따로 서비스 클래스를 나눠서 작성할지, 지금처럼 해당 클래스에 명시할지 의논하는게 좋을 것 같습니다.
  public void syncEnabledIndices() {
    log.info("[AutoSync] 자동 연동 활성화 지수 조회 시작");

    List<AutoSyncConfig> enabledConfigs =
        autoSyncConfigRepository.findByEnabledTrue();

    log.info("[AutoSync] 조회된 자동 연동 대상 개수: {}", enabledConfigs.size());

    for (AutoSyncConfig config : enabledConfigs) {

      try {
        syncIndex(config);
      } catch (Exception e) {
        log.error("[AutoSync] 지수 동기화 실패: configId = {}", config.getId(), e);
      }
    }
  }

  private void syncIndex(AutoSyncConfig config) {
    log.info("[AutoSync] 지수 동기화 실행: configId = {}", config.getId());
  }



  @Transactional(readOnly = true)
  public AutoSyncConfigListResponse getList(AutoSyncConfigListRequest condition) {

    List<AutoSyncConfig> configs = autoSyncConfigRepository.findListByCursor(condition);

    long totalElements = autoSyncConfigRepository.countByCondition(condition);

    boolean hasNext = configs.size() > condition.getSize();

    // 4. 다음 페이지가 있다면, 실제 응답에 넣을 때는 마지막 1개(확인용)를 뺌
    if (hasNext) {
      configs.remove(configs.size() - 1);
    }

    // 5. Entity -> DTO 변환
    List<AutoSyncConfigDto> dtoList = configs.stream()
        .map(autoSyncConfigMapper::toDto)
        .collect(java.util.stream.Collectors.toList());

    // 6. 다음 페이지 요청 시 사용할 cursor와 nextIdAfter 값 추출
    String nextCursor = null;
    Long nextIdAfter = null;

    if (!dtoList.isEmpty()) {
      AutoSyncConfigDto lastItem = dtoList.get(dtoList.size() - 1);

      // AutoSyncConfigDto의 id가 Integer이므로 Long으로 안전하게 변환
      nextIdAfter = lastItem.getId() != null ? Long.valueOf(lastItem.getId()) : null;

      if ("enabled".equals(condition.getSortField())) {
        nextCursor = String.valueOf(lastItem.isEnabled());
      } else {
        nextCursor = lastItem.getIndexName();
      }
    }

    return new AutoSyncConfigListResponse(
        dtoList,               // content
        nextCursor,            // nextCursor
        nextIdAfter,           // nextIdAfter
        condition.getSize(),   // size
        totalElements,         // totalElements
        hasNext                // hasNext
    );

  }
}
