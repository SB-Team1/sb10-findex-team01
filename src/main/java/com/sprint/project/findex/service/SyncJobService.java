package com.sprint.project.findex.service;

import com.sprint.project.findex.dto.openapi.StockMarketIndexRequest;
import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse;
import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse.StockIndexDto;
import com.sprint.project.findex.dto.syncjob.CursorPageResponseSyncJobDto;
import com.sprint.project.findex.dto.syncjob.IndexDataSyncRequest;
import com.sprint.project.findex.dto.syncjob.SyncJobDto;
import com.sprint.project.findex.dto.syncjob.SyncJobRequestQuery;
import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.entity.SyncJob;
import com.sprint.project.findex.mapper.SyncJobMapper;
import com.sprint.project.findex.repository.IndexInfoRepository;
import com.sprint.project.findex.repository.SyncJobRepository;
import com.sprint.project.findex.service.openapi.internal.PersistentWorker;
import jakarta.servlet.http.HttpServletRequest;
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

@Service
@RequiredArgsConstructor
public class SyncJobService {

  private final IndexSyncService indexSyncService;
  private final IndexInfoRepository indexInfoRepository;
  private final SyncJobRepository syncJobRepository;
  private final SyncJobMapper syncJobMapper;
  private final PersistentWorker worker;


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
      StockMarketIndexResponse openApiResponse = indexSyncService.fetchStockIndex(
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
    List<IndexInfo> indexInfos = indexInfoRepository.findByIdIn(
        indexDataSyncRequest.indexInfoIds());
    String requestIpAddr = request.getRemoteAddr();

    // 지수 정보를 바탕으로 Open API로부터 지수 데이터 연동 작업을 한다.
    for (IndexInfo indexInfo : indexInfos) {
      response.addAll(
          indexSyncService.syncIndexData(indexDataSyncRequest, indexInfo, requestIpAddr)
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

      StockMarketIndexResponse stockMarketIndexResponse = indexSyncService.fetchStockIndex(
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
}
