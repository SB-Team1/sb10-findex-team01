package com.sprint.project.findex.service;

import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigDto;
import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigUpdateRequest;
import com.sprint.project.findex.entity.AutoSyncConfig;
import com.sprint.project.findex.mapper.AutoSyncConfigMapper;
import com.sprint.project.findex.repository.AutoSyncConfigRepository;
import com.sprint.project.findex.IndexInfo;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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



}
