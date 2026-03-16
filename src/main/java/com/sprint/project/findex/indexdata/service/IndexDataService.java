package com.sprint.project.findex.indexdata.service;

import com.sprint.project.findex.global.entity.DeletedStatus;
import com.sprint.project.findex.global.entity.SourceType;
import com.sprint.project.findex.indexdata.dto.IndexDataCreateRequest;
import com.sprint.project.findex.indexdata.dto.IndexDataDto;
import com.sprint.project.findex.indexdata.entity.IndexData;
import com.sprint.project.findex.indexdata.mapper.IndexDataMapper;
import com.sprint.project.findex.indexdata.repository.IndexDataRepository;
import com.sprint.project.findex.indexinfo.entity.IndexInfo;
import com.sprint.project.findex.indexinfo.repository.IndexInfoRepository;
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

    IndexInfo indexInfo = indexInfoRepository.findById(request.indexInfoId())
        .orElseThrow(() -> new NoSuchElementException("지수 정보를 찾을 수 없습니다."));

    validateDuplicateData(request, indexInfo);

    IndexData indexData = new IndexData(
        indexInfo,
        request.baseDate(),
        SourceType.USER,
        request.marketPrice(),
        request.closingPrice(),
        request.highPrice(),
        request.lowPrice(),
        request.versus(),
        request.fluctuationRate(),
        request.tradingQuantity(),
        request.tradingPrice(),
        request.marketTotalAmount()
    );

    indexDataRepository.save(indexData);

    return indexDataMapper.toDto(indexData);
  }

  private void validateDuplicateData(IndexDataCreateRequest request, IndexInfo indexInfo) {
    boolean exists = indexDataRepository.existsByIndexInfoAndBaseDateAndIsDeleted(indexInfo,
        request.baseDate(), DeletedStatus.ACTIVE);
    if (exists) {
      throw new IllegalArgumentException("이미 존재하는 지수 데이터 입니다.");
    }
  }

}
