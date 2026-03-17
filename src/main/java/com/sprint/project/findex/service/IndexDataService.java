package com.sprint.project.findex.service;

import com.sprint.project.findex.dto.indexdata.CursorPageIndexDataRequest;
import com.sprint.project.findex.dto.indexdata.CursorPageResponseIndexDataDto;
import com.sprint.project.findex.dto.indexdata.IndexDataCreateRequest;
import com.sprint.project.findex.dto.indexdata.IndexDataDto;
import com.sprint.project.findex.dto.indexdata.IndexDataUpdateRequest;
import com.sprint.project.findex.entity.DeletedStatus;
import com.sprint.project.findex.entity.IndexData;
import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.entity.SourceType;
import com.sprint.project.findex.mapper.IndexDataMapper;
import com.sprint.project.findex.repository.IndexDataRepository;
import com.sprint.project.findex.repository.IndexInfoRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class IndexDataService {

  private final IndexDataRepository indexDataRepository;
  private final IndexInfoRepository indexInfoRepository;
  private final IndexDataMapper indexDataMapper;

  public IndexDataDto createByUser(IndexDataCreateRequest request) {

    //todo 임시로 findById를 호출하고 있음, 추후 Soft Delete 로직 적용 시 달라질 수 있음
    IndexInfo indexInfo = indexInfoRepository.findById(request.indexInfoId())
        .orElseThrow(() -> new NoSuchElementException("지수 정보를 찾을 수 없습니다."));

    validateDuplicateData(request, indexInfo);

    IndexData indexData = IndexData.builder()
        .indexInfo(indexInfo)
        .baseDate(request.baseDate())
        .sourceType(SourceType.USER)
        .marketPrice(request.marketPrice())
        .closingPrice(request.closingPrice())
        .highPrice(request.highPrice())
        .lowPrice(request.lowPrice())
        .versus(request.versus())
        .fluctuationRate(request.fluctuationRate())
        .tradingQuantity(request.tradingQuantity())
        .tradingPrice(request.tradingPrice())
        .marketTotalAmount(request.marketTotalAmount())
        .isDeleted(DeletedStatus.ACTIVE)
        .build();

    indexDataRepository.save(indexData);

    return indexDataMapper.toDto(indexData);
  }

  public IndexDataDto update(Long id, IndexDataUpdateRequest request) {
    IndexData indexData = indexDataRepository.findByIdAndIsDeleted(id, DeletedStatus.ACTIVE)
        .orElseThrow(() -> new NoSuchElementException("지수 데이터를 찾을 수 없습니다."));
    indexData.update(request);

    return indexDataMapper.toDto(indexData);
  }

  public void delete(Long id) {
    IndexData indexData = indexDataRepository.findByIdAndIsDeleted(id, DeletedStatus.ACTIVE)
        .orElseThrow(() -> new NoSuchElementException("지수 데이터를 찾을 수 없습니다."));
    indexData.updateIsDeleted(DeletedStatus.DELETED);
  }

  public CursorPageResponseIndexDataDto findAll(CursorPageIndexDataRequest request) {
    Slice<IndexData> slice = indexDataRepository.findCursorPage(request);
    List<IndexData> content = slice.getContent();

    String nextCursor = null;
    Long nextIdAfter = null;
    boolean hasNext = slice.hasNext();

    if (!content.isEmpty()) {
      IndexData lastValue = content.get(content.size() - 1);
      nextCursor = request.sortField().extractValueToString(lastValue);
      nextIdAfter = lastValue.getId();
    }

    List<IndexDataDto> indexDataDtoList = content.stream()
        .map(indexDataMapper::toDto)
        .toList();

    Long totalElements = indexDataRepository.countByRequest(request);

    return new CursorPageResponseIndexDataDto(
        indexDataDtoList,
        nextCursor,
        nextIdAfter,
        indexDataDtoList.size(),
        totalElements,
        hasNext
    );
  }

  private void validateDuplicateData(IndexDataCreateRequest request, IndexInfo indexInfo) {
    boolean exists = indexDataRepository.existsByIndexInfoAndBaseDateAndIsDeleted(indexInfo,
        request.baseDate(), DeletedStatus.ACTIVE);
    if (exists) {
      throw new IllegalArgumentException("이미 존재하는 지수 데이터 입니다.");
    }
  }
}
