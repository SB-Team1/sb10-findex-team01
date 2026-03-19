package com.sprint.project.findex.service;

import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse;
import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse.StockIndexDto;
import com.sprint.project.findex.entity.IndexData;
import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.entity.SourceType;
import com.sprint.project.findex.entity.SyncJob;
import com.sprint.project.findex.global.entity.JobType;
import com.sprint.project.findex.global.entity.ResultType;
import com.sprint.project.findex.global.exception.ApiException;
import com.sprint.project.findex.global.exception.ErrorCode;
import com.sprint.project.findex.repository.indexdata.IndexDataRepository;
import com.sprint.project.findex.repository.syncjob.SyncJobRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IndexSyncService {

  private final IndexDataRepository indexDataRepository;
  private final SyncJobRepository syncJobRepository;


  // 지수 데이터 연동에 대해 트랜잭션을 처리하기 위한 서비스
  @Transactional
  public List<SyncJob> saveIndexDataAndSyncJobs(Map<String, IndexData> indexDataMap,
      List<StockIndexDto> stockIndexDtos,
      IndexInfo indexInfo, String requestIpAddr) {

    List<IndexData> newIndexDatas = new ArrayList<>();
    List<SyncJob> syncJobs = new ArrayList<>();

    for (StockIndexDto stockIndexDto : stockIndexDtos) {

      // 지수 데이터 갱신 or 생성
      IndexData data = putIndexData(stockIndexDto, indexInfo, indexDataMap);
      if (data != null) {
        newIndexDatas.add(data);
      }

      syncJobs.add(
          new SyncJob(indexInfo, JobType.INDEX_DATA, stockIndexDto.baseDate(), requestIpAddr,
              ResultType.SUCCESS)
      );
    }

    // 영속화
    if (!newIndexDatas.isEmpty()) {
      indexDataRepository.saveAll(newIndexDatas);
    }
    if (!syncJobs.isEmpty()) {
      syncJobRepository.saveAll(syncJobs);
    }

    return syncJobs;

  }

  private IndexData putIndexData(
      StockIndexDto dto,
      IndexInfo indexInfo,
      Map<String, IndexData> indexDataMap
  ) {

    String key = createIndexDataKey(indexInfo.getId(), dto.baseDate());
    IndexData indexData = indexDataMap.get(key);

    if (indexData != null) {
      indexData.update(dto);
      return null;
    }

    indexData = IndexData.builder()
        .indexInfo(indexInfo)
        .baseDate(dto.baseDate())
        .sourceType(SourceType.OPEN_API)
        .marketPrice(dto.marketPrice())
        .closingPrice(dto.closingPrice())
        .highPrice(dto.highPrice())
        .lowPrice(dto.lowPrice())
        .versus(dto.versus())
        .fluctuationRate(dto.fluctuationRate())
        .tradingPrice(dto.tradingPrice())
        .tradingQuantity(dto.tradingQuantity())
        .marketTotalAmount(dto.marketTotalAmount())
        .build();

    return indexData;
  }


  public List<StockIndexDto> extractDtoListFromResponse(StockMarketIndexResponse response) {
    // 응답 형태가 맞지 않은 경우
    if (response == null ||
        response.response() == null ||
        response.response().body() == null ||
        response.response().body().items() == null ||
        response.response().body().items().item() == null) {
      throw new ApiException(ErrorCode.OPEN_API_INVALID_RESPONSE);
    }

    // 에러코드가 온 경우
    if (!"00".equals(response.response().header().resultCode())) {
      throw new ApiException(ErrorCode.OPEN_API_INVALID_RESPONSE);
    }

    return response.response().body().items().item();
  }

  // 응답 item에서 첫번째 값만 가져온다
  private StockIndexDto extractDtoFromResponse(StockMarketIndexResponse response)
      throws ApiException {
    List<StockIndexDto> stockIndexDtoList = this.extractDtoListFromResponse(response);

    if (stockIndexDtoList.isEmpty()) {
      return null;
    }

    return stockIndexDtoList.get(0);
  }

  private String createIndexDataKey(Long id, LocalDate targetDate) {
    return id + "_" + targetDate;
  }
}
