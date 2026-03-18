package com.sprint.project.findex.service;

import com.sprint.project.findex.dto.indexdata.CursorPageIndexDataRequest;
import com.sprint.project.findex.dto.indexdata.CursorPageResponseIndexDataDto;
import com.sprint.project.findex.dto.indexdata.IndexDataCreateRequest;
import com.sprint.project.findex.dto.indexdata.IndexDataDto;
import com.sprint.project.findex.dto.indexdata.IndexDataUpdateRequest;
import com.sprint.project.findex.entity.IndexData;
import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.entity.SourceType;
import com.sprint.project.findex.global.exception.ApiException;
import com.sprint.project.findex.global.exception.ErrorCode;
import com.sprint.project.findex.mapper.IndexDataMapper;
import com.sprint.project.findex.repository.IndexDataRepository;
import com.sprint.project.findex.repository.IndexInfoRepository;
import java.util.List;
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

    IndexInfo indexInfo = indexInfoRepository.findById(request.indexInfoId())
        .orElseThrow(() -> new ApiException(ErrorCode.INDEX_INFO_ID_NOT_FOUND));


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
        .build();

    indexDataRepository.save(indexData);

    return indexDataMapper.toDto(indexData);
  }

  public IndexDataDto update(Long id, IndexDataUpdateRequest request) {
    IndexData indexData = indexDataRepository.findById(id)
        .orElseThrow(() -> new ApiException(ErrorCode.INDEX_DATA_NOT_FOUND));
    indexData.update(request);

    return indexDataMapper.toDto(indexData);
  }

  public void delete(Long id) {
    IndexData indexData = indexDataRepository.findById(id)
        .orElseThrow(() -> new ApiException(ErrorCode.INDEX_DATA_NOT_FOUND));
    indexDataRepository.delete(indexData);
  }

  @Transactional(readOnly = true)
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
}
