package com.sprint.project.findex.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.project.findex.dto.openapi.StockMarketIndexRequest;
import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse;
import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse.StockIndexDto;
import com.sprint.project.findex.dto.syncjob.CursorPageResponseSyncJobDto;
import com.sprint.project.findex.dto.syncjob.IndexDataSyncRequest;
import com.sprint.project.findex.dto.syncjob.SyncJobDto;
import com.sprint.project.findex.dto.syncjob.SyncJobRequestQuery;
import com.sprint.project.findex.entity.IndexData;
import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.entity.SyncJob;
import com.sprint.project.findex.global.exception.ApiException;
import com.sprint.project.findex.global.exception.ErrorCode;
import com.sprint.project.findex.mapper.SyncJobMapper;
import com.sprint.project.findex.repository.indexdata.IndexDataRepository;
import com.sprint.project.findex.repository.indexinfo.IndexInfoRepository;
import com.sprint.project.findex.repository.syncjob.SyncJobRepository;
import com.sprint.project.findex.service.openapi.internal.PersistentWorker;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class SyncJobService {

  private final IndexSyncService indexSyncService;
  private final IndexInfoRepository indexInfoRepository;
  private final IndexDataRepository indexDataRepository;
  private final SyncJobRepository syncJobRepository;
  private final SyncJobMapper syncJobMapper;
  private final ObjectMapper objectMapper;
  private final PersistentWorker worker;
  private final WebClient openapi;

  private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.BASIC_ISO_DATE;


  // 가장 최신의 지수 정보를 로드해 저장합니다.
  public List<SyncJobDto> syncIndexInfos(HttpServletRequest request) {

    List<SyncJob> response = new ArrayList<>();
    String requestIpAddr = request.getRemoteAddr();
    LocalDate baseDate = getLastValidDay();

    // DB에서 지수 정보 전체를 map으로 미리 불러오기 (key는 unique 체크)
    Map<String, IndexInfo> indexInfoMap = indexInfoRepository.findAll()
        .stream()
        .collect(Collectors.toMap(
            indexInfo -> createIndexInfoKey(indexInfo.getIndexName(),
                indexInfo.getIndexClassification()),
            Function.identity(),
            (existing, duplicate) -> existing // 중복키 입력 무시
        ));

    boolean hasMore = true;
    int pageNo = 1;

    while (hasMore) {
      StockMarketIndexResponse openApiResponse = fetchStockIndex(
          StockMarketIndexRequest.builder()
              .pageNo(pageNo)
              .numOfRows(50)
              .baseDate(baseDate.format(DateTimeFormatter.BASIC_ISO_DATE))
              .build()
      );

      List<StockIndexDto> stockIndexDtoList = indexSyncService.extractDtoListFromResponse(
          openApiResponse);
      if (stockIndexDtoList == null || stockIndexDtoList.isEmpty()) {
        hasMore = false;
        continue;
      }

      // worker 이용하여 IndexInfo와 SyncJob를 한 트랜잭션 내에서 함께 저장
      response.addAll(
          worker.saveIndexInfoAndSyncJob(stockIndexDtoList, indexInfoMap, requestIpAddr)
      );

      pageNo++;
    }

    return response.stream()
        .map(syncJobMapper::toDto)
        .toList();
  }

  public List<SyncJobDto> syncIndexData(IndexDataSyncRequest indexDataSyncRequest,
      HttpServletRequest request) {

    List<SyncJob> response = new ArrayList<>();
    String requestIpAddr = request.getRemoteAddr();

    List<IndexInfo> indexInfos = indexInfoRepository.findByIdIn(
        indexDataSyncRequest.indexInfoIds());

    for (IndexInfo indexInfo : indexInfos) {
      // 현재 지수 정보에 대항하는 지수 데이터 미리 불러오기
      Map<String, IndexData> indexDataMap = indexDataRepository.findByIndexInfoAndBaseDateBetween(
              indexInfo, indexDataSyncRequest.baseDateFrom(), indexDataSyncRequest.baseDateTo())
          .stream().collect(Collectors.toMap(
              idxData -> createIndexDataKey(idxData.getIndexInfo().getId(), idxData.getBaseDate()),
              Function.identity()
          ));

      // 조건에 맞는 지수 데이터 받아오기
      LocalDate baseDateFrom = indexDataSyncRequest.baseDateFrom();
      LocalDate baseDateTo = indexDataSyncRequest.baseDateTo();
      List<StockIndexDto> stockIndexDtos = getIndexDataFromOpenAPI(indexInfo, baseDateFrom,
          baseDateTo);

      // 영속화
      response.addAll(
          indexSyncService.saveIndexDataAndSyncJobs(indexDataMap, stockIndexDtos, indexInfo,
              requestIpAddr)
      );
    }

    return response.stream()
        .map(syncJobMapper::toDto)
        .toList();
  }

  @Transactional(readOnly = true)
  public CursorPageResponseSyncJobDto findSyncJobs(SyncJobRequestQuery query) {
    List<SyncJob> syncJobs = syncJobRepository.search(query);

    // 다음 페이지 유무 확인
    boolean hasNext = syncJobs.size() > query.size();
    List<SyncJob> pagedSyncJobs = hasNext
        ? syncJobs.subList(0, query.size())
        : syncJobs;

    // 마지막 요소 ID와 cursor 설정
    Long nextIdAfter = null;
    String nextCursor = null;

    if (!pagedSyncJobs.isEmpty()) {
      SyncJob lastItem = pagedSyncJobs.get(pagedSyncJobs.size() - 1);

      if (hasNext) {
        nextIdAfter = lastItem.getId();

        if ("jobTime".equals(query.sortField())) {
          nextCursor = lastItem.getJobTime().toString();
        } else {
          LocalDate targetDate = lastItem.getTargetDate();
          nextCursor = (targetDate != null) ? targetDate.toString() : null;
        }
      }
    }

    // 연동 작업 전체 카운트
    long totalElements = syncJobRepository.countWithFilter(query);

    // 응답 콘텐츠 변환(엔티티 -> dto)
    List<SyncJobDto> content = pagedSyncJobs.stream()
        .map(syncJobMapper::toDto)
        .toList();

    return CursorPageResponseSyncJobDto.builder()
        .content(content)
        .nextCursor(nextCursor)
        .nextIdAfter(nextIdAfter)
        .size(content.size())
        .totalElements(totalElements)
        .hasNext(hasNext)
        .build();
  }

  // OpenAPI에서 유효한 값을 줄 수 있는 가장 최신의 날짜 구하기
  private LocalDate getLastValidDay() {
    LocalDate baseDate = LocalDate.now().minusDays(1);
    LocalDate minimumDate = baseDate.minusDays(30); // 30일 전까지만 확인함

    while (!baseDate.isBefore(minimumDate)) {
      // api 요청 파라미터 설정
      StockMarketIndexRequest stockMarketIndexRequest = StockMarketIndexRequest.builder()
          .baseDate(baseDate.format(DateTimeFormatter.BASIC_ISO_DATE))
          .build();

      StockMarketIndexResponse stockMarketIndexResponse = fetchStockIndex(
          stockMarketIndexRequest
      );

      List<StockIndexDto> responseItems = indexSyncService.extractDtoListFromResponse(
          stockMarketIndexResponse);

      if (responseItems != null && !responseItems.isEmpty()) {
        return baseDate;
      }

      baseDate = baseDate.minusDays(1);
    }

    // exception 대신 오늘 날짜 리턴하도록 함
    return LocalDate.now();
  }

  private String createIndexInfoKey(String indexName, String indexClassification) {
    return indexName + "_" + indexClassification;
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

  private int getTotalCount(StockMarketIndexResponse stockMarketIndexResponse) {
    return stockMarketIndexResponse.response().body().totalCount();

  }

  private List<StockIndexDto> getIndexDataFromOpenAPI(IndexInfo indexInfo, LocalDate baseDateFrom,
      LocalDate baseDateTo) {
    // openapi로부터 빈 리스트가 올 때까지 반복적으로 받기
    List<StockIndexDto> stockIndexDtos = new ArrayList<>();

    int pageNo = 1;
    int numOfRows = 30;

    // 최초 호출
    StockMarketIndexResponse firstResponse = fetchStockIndex(
        StockMarketIndexRequest.builder()
            .pageNo(pageNo)
            .numOfRows(numOfRows)
            .indexName(indexInfo.getIndexName())
            .beginEmployedItemsCount(indexInfo.getEmployedItemsCount())
            .endEmployedItemsCount(indexInfo.getEmployedItemsCount() + 1)
            .beginBaseDate(baseDateFrom.format(dateTimeFormatter))
            .endBaseDate(baseDateTo.format(dateTimeFormatter))
            .build()
    );
    List<StockIndexDto> firstItems = extractDtoListFromResponse(firstResponse);
    stockIndexDtos.addAll(firstItems);

    // 반복 호출 횟수 계산
    int totalCount = getTotalCount(firstResponse);
    int totalPages = (int) Math.ceil((double) totalCount / numOfRows);

    // 나머지 데이터 요청
    for (pageNo = 2; pageNo <= totalPages; pageNo++) {
      StockMarketIndexResponse openApiResponse = fetchStockIndex(
          StockMarketIndexRequest.builder()
              .pageNo(pageNo)
              .numOfRows(numOfRows)
              .indexName(indexInfo.getIndexName())
              .beginEmployedItemsCount(indexInfo.getEmployedItemsCount())
              .endEmployedItemsCount(indexInfo.getEmployedItemsCount() + 1)
              .beginBaseDate(baseDateFrom.format(dateTimeFormatter))
              .endBaseDate(baseDateTo.format(dateTimeFormatter))
              .build()
      );

      List<StockIndexDto> openApiResponseItems = extractDtoListFromResponse(openApiResponse);
      stockIndexDtos.addAll(openApiResponseItems);
    }

    return stockIndexDtos;
  }

  private String createIndexDataKey(Long id, LocalDate targetDate) {
    return id + "_" + targetDate;
  }
}
