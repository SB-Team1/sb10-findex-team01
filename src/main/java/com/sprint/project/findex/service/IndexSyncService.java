package com.sprint.project.findex.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.project.findex.dto.openapi.StockMarketIndexRequest;
import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse;
import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse.StockIndexDto;
import com.sprint.project.findex.dto.syncjob.IndexDataSyncRequest;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class IndexSyncService {

  private final IndexDataRepository indexDataRepository;
  private final SyncJobRepository syncJobRepository;
  private final ObjectMapper objectMapper;
  private final WebClient openapi;

  @Transactional
  // 지수 데이터 연동에 대해 트랜잭션을 처리하기 위한 서비스
  public List<SyncJob> syncIndexData(IndexDataSyncRequest indexDataSyncRequest,
      IndexInfo indexInfo, String requestIpAddr) {

    List<IndexData> toInsert = new ArrayList<>();
    List<SyncJob> syncJobs = new ArrayList<>();

    // 현재 지수 정보에 대항하는 지수 데이터 미리 불러오기
    Map<String, IndexData> indexDataMap = indexDataRepository.findByIndexInfoAndBaseDateBetween(
            indexInfo, indexDataSyncRequest.baseDateFrom(), indexDataSyncRequest.baseDateTo())
        .stream().collect(Collectors.toMap(
            idxData -> createIndexDataKey(idxData.getIndexInfo().getId(), idxData.getBaseDate()),
            Function.identity()
        ));

    // 해당 기간에 대해 정보를 요청한다.
    LocalDate targetDate = indexDataSyncRequest.baseDateFrom();
    LocalDate baseDateTo = indexDataSyncRequest.baseDateTo();

    while (!targetDate.isAfter(baseDateTo)) {

      LocalDate finalTargetDate = targetDate;

      try {
        // OepnAPI에 지수 데이터 요청
        StockMarketIndexResponse openApiResponse = fetchStockIndex(
            StockMarketIndexRequest.builder()
                .pageNo(1)
                .numOfRows(10)
                .indexName(indexInfo.getIndexName())
                .baseDate(targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .build()
        );

        StockIndexDto stockIndexDto = extractDtoFromResponse(openApiResponse);

        // 지수 데이터 및 연동 기록 등록
        Optional.ofNullable(stockIndexDto)
            .ifPresent(dto -> putIndexData(
                    dto,
                    indexInfo,
                    finalTargetDate,
                    indexDataMap,
                    toInsert,
                    syncJobs,
                    requestIpAddr
                )
            );

      } catch (Exception e) {
        // 실패 기록
        syncJobs.add(
            new SyncJob(indexInfo, JobType.INDEX_DATA,
                finalTargetDate, requestIpAddr, ResultType.FAILED)
        );
      } finally {
        targetDate = targetDate.plusDays(1);
      }
    }

    // 영속화
    if (!toInsert.isEmpty()) {
      indexDataRepository.saveAll(toInsert);
    }
    if (!syncJobs.isEmpty()) {
      syncJobRepository.saveAll(syncJobs);
    }

    return syncJobs;

  }

  private void putIndexData(
      StockIndexDto dto,
      IndexInfo indexInfo,
      LocalDate targetDate,
      Map<String, IndexData> indexDataMap,
      List<IndexData> toInsert,
      List<SyncJob> syncJobs,
      String requestIpAddr
  ) {

    String key = createIndexDataKey(indexInfo.getId(), targetDate);
    IndexData indexData = indexDataMap.get(key);

    if (indexData != null) {
      indexData.update(dto); // dirty checking
    } else {
      indexData = IndexData.builder()
          .indexInfo(indexInfo)
          .baseDate(targetDate)
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

      toInsert.add(indexData);

    }

    syncJobs.add(
        new SyncJob(indexInfo, JobType.INDEX_DATA, targetDate, requestIpAddr,
            ResultType.SUCCESS)
    );
  }


  public StockMarketIndexResponse fetchStockIndex(StockMarketIndexRequest request) {
    Map<String, Object> queryParams = objectMapper.convertValue(request, new TypeReference<>() {
    });

    try {
      return openapi.get()
          .uri(uriBuilder -> {
            queryParams.forEach((k, v) -> {
              if (v != null) {
                uriBuilder.queryParam(k, URLEncoder.encode(v.toString(), StandardCharsets.UTF_8));
              }
            });
            return uriBuilder.build();
          })
          .retrieve()
          .bodyToMono(StockMarketIndexResponse.class)
          .block(Duration.ofSeconds(5));

    } catch (Exception e) {
      throw new ApiException(ErrorCode.OPEN_API_REQUEST_FAILED, e.getMessage());
    }
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
