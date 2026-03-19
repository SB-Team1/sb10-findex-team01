package com.sprint.project.findex.service;

import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigDto;
import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigListRequest;
import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigListResponse;
import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigUpdateRequest;
import com.sprint.project.findex.entity.AutoSyncConfig;
import com.sprint.project.findex.global.exception.ApiException;
import com.sprint.project.findex.global.exception.ErrorCode;
import com.sprint.project.findex.mapper.AutoSyncConfigMapper;
import com.sprint.project.findex.repository.indexinfo.IndexInfoRepository;
import com.sprint.project.findex.repository.autosyncconfig.AutoSyncConfigRepository;
import com.sprint.project.findex.entity.IndexInfo;
import java.util.List;
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
  private final IndexInfoRepository indexInfoRepository;

  @Transactional
  public AutoSyncConfigDto create(IndexInfo indexInfo) {
    if (indexInfo == null) {
      throw new ApiException(ErrorCode.INVALID_PARAMETER, "지수 정보가 존재하지 않습니다.");
    }

    AutoSyncConfig autoSyncConfig = new AutoSyncConfig(indexInfo);
    AutoSyncConfig savedConfig = autoSyncConfigRepository.save(autoSyncConfig);

    return autoSyncConfigMapper.toDto(savedConfig);
  }

  public void createAll(List<IndexInfo> indexInfos) {
    List<AutoSyncConfig> configs = indexInfos.stream()
        .map(indexInfo -> AutoSyncConfig.builder().indexInfo(indexInfo).build())
        .toList();
    autoSyncConfigRepository.saveAll(configs);
  }

  @Transactional
  public AutoSyncConfigDto register(AutoSyncConfigDto request) {

    IndexInfo indexInfo = indexInfoRepository.findById(request.getIndexInfoId())
        .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "지수 정보가 없습니다."));

    AutoSyncConfig autoSyncConfig = AutoSyncConfig.builder()
        .indexInfo(indexInfo)
        .enabled(request.isEnabled()) // 사용자가 입력한 활성화 여부 적용
        // 추가 속성들 세팅...
        .build();

    AutoSyncConfig savedConfig = autoSyncConfigRepository.save(autoSyncConfig);
    return autoSyncConfigMapper.toDto(savedConfig);
  }

  @Transactional
  public AutoSyncConfigDto update(Long id, AutoSyncConfigUpdateRequest request) {
    AutoSyncConfig autoSyncConfig = getAutoSyncConfig(id);
    autoSyncConfig.updateEnabled(request.isEnabled());

    return autoSyncConfigMapper.toDto(autoSyncConfig);
  }

  @Transactional(readOnly = true)
  public List<AutoSyncConfig> getEnabledConfigs() {
    return autoSyncConfigRepository.findByEnabledTrue();
  }

  private AutoSyncConfig getAutoSyncConfig(Long id) {
    return autoSyncConfigRepository.findById(id)
        .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 자동 연동 설정입니다. ID: " + id));
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
    List<AutoSyncConfigDto> dtoList = configs.stream().map(autoSyncConfigMapper::toDto).toList();

    // 6. 다음 페이지 요청 시 사용할 cursor와 nextIdAfter 값 추출
    String nextCursor = null;
    Long nextIdAfter = null;

    if (!dtoList.isEmpty()) {
      AutoSyncConfigDto lastItem = dtoList.get(dtoList.size() - 1);
      nextIdAfter = lastItem.getId() != null ? lastItem.getId() : null;

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
