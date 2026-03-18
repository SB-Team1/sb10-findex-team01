package com.sprint.project.findex.service;

import com.sprint.project.findex.dto.indexinfo.CursorPageResponseIndexInfoDto;
import com.sprint.project.findex.dto.indexinfo.IndexInfoCreateRequest;
import com.sprint.project.findex.dto.indexinfo.IndexInfoCursorPageRequest;
import com.sprint.project.findex.dto.indexinfo.IndexInfoDto;
import com.sprint.project.findex.dto.indexinfo.IndexInfoSummaryDto;
import com.sprint.project.findex.dto.indexinfo.IndexInfoUpdateRequest;
import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.mapper.IndexInfoMapper;
import com.sprint.project.findex.repository.IndexInfoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class IndexInfoService {

  private final IndexInfoRepository indexInfoRepository;
  private final IndexInfoMapper indexInfoMapper;

  public IndexInfoDto create(IndexInfoCreateRequest request) {
    IndexInfo indexInfo = indexInfoMapper.toEntity(request);
    indexInfoRepository.save(indexInfo);
    return indexInfoMapper.toDto(indexInfo);
  }

  @Transactional(readOnly = true)
  public IndexInfoDto find(Long id) {
    // todo: exception
    IndexInfo indexInfo = indexInfoRepository.findById(id).orElseThrow();
    return indexInfoMapper.toDto(indexInfo);
  }

  public void delete(Long id) {
    // todo: IllegalArgumentException, OptimisticLockingFailureException
    indexInfoRepository.deleteById(id);
  }

  public IndexInfoDto update(Long id, IndexInfoUpdateRequest request) {
    // todo: exception
    IndexInfo indexInfo = indexInfoRepository.findById(id).orElseThrow();
    indexInfo.update(request);
    return indexInfoMapper.toDto(indexInfo);
  }

  // todo
  @Transactional(readOnly = true)
  public CursorPageResponseIndexInfoDto findWithCursorPage(IndexInfoCursorPageRequest request) {
    List<IndexInfo> results = indexInfoRepository.findByCursor(request);
    return toCursorPageDto(results, request);
  }

  @Transactional(readOnly = true)
  public List<IndexInfoSummaryDto> findIndexInfoSummary() {
    return indexInfoRepository.findDistinctClassificationsAndNames();
  }

  private CursorPageResponseIndexInfoDto toCursorPageDto(List<IndexInfo> indexInfos,
      IndexInfoCursorPageRequest request) {
    List<IndexInfoDto> content = indexInfoMapper.toDtoList(indexInfos);
    String nextCursor = "";
    Long nextIdAfter = null;
    int pageSize = request.getSize();
    Long totalElements = indexInfoRepository.getTotalElements(request);
    boolean hasNext = false;
    if (content.size() > pageSize) {
      IndexInfoDto indexInfoDto = content.get(pageSize);
      nextCursor = indexInfoDto.indexClassification();
      nextIdAfter = indexInfoDto.id();
      content.remove(pageSize);
      hasNext = true;
    }
    return CursorPageResponseIndexInfoDto.builder()
        .content(content)
        .nextCursor(nextCursor)
        .nextIdAfter(nextIdAfter)
        .size(pageSize)
        .totalElements(totalElements)
        .hasNext(hasNext)
        .build();
  }
}
