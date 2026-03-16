package com.sprint.project.findex.service;

import com.sprint.project.findex.dto.IndexDataCreateRequest;
import com.sprint.project.findex.dto.IndexDataDto;
import com.sprint.project.findex.dto.IndexDataUpdateRequest;
import com.sprint.project.findex.entity.DeletedStatus;
import com.sprint.project.findex.entity.IndexData;
import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.entity.SourceType;
import com.sprint.project.findex.mapper.IndexDataMapper;
import com.sprint.project.findex.repository.IndexDataRepository;
import com.sprint.project.findex.repository.IndexInfoRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
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
    indexData.updateMarketPrice(request.marketPrice());
    indexData.updateClosingPrice(request.closingPrice());
    indexData.updateHighPrice(request.highPrice());
    indexData.updateLowPrice(request.lowPrice());
    indexData.updateVersus(request.versus());
    indexData.updateFluctuationRate(request.fluctuationRate());
    indexData.updateTradingQuantity(request.tradingQuantity());
    indexData.updateTradingPrice(request.tradingPrice());
    indexData.updateMarketTotalAmount(request.marketTotalAmount());

    indexData.updateSourceTypeToUser(); // 소스타입 사용자로 변경

    return indexDataMapper.toDto(indexData);
  }

  public void delete(Long id) {
    IndexData indexData = indexDataRepository.findByIdAndIsDeleted(id, DeletedStatus.ACTIVE)
        .orElseThrow(() -> new NoSuchElementException("지수 데이터를 찾을 수 없습니다."));
    indexData.updateIsDeleted(DeletedStatus.DELETED);
  }

  private void validateDuplicateData(IndexDataCreateRequest request, IndexInfo indexInfo) {
    boolean exists = indexDataRepository.existsByIndexInfoAndBaseDateAndIsDeleted(indexInfo,
        request.baseDate(), DeletedStatus.ACTIVE);
    if (exists) {
      throw new IllegalArgumentException("이미 존재하는 지수 데이터 입니다.");
    }
  }

}
