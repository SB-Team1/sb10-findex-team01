package com.sprint.project.findex.service;

import com.sprint.project.findex.dto.SyncJobDto;
import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse;
import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse.StockIndexDto;
import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.entity.SyncJob;
import com.sprint.project.findex.global.exception.BusinessLogicException;
import com.sprint.project.findex.global.exception.ExceptionCode;
import com.sprint.project.findex.mapper.SyncJobMapper;
import com.sprint.project.findex.repository.IndexInfoRepository;
import com.sprint.project.findex.service.openapi.internal.PersistentWorker;
import jakarta.servlet.http.HttpServletRequest;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class SyncJobService {

  private final IndexInfoRepository indexInfoRepository;
  private final SyncJobMapper syncJobMapper;
  private final PersistentWorker worker;

  @Qualifier("openapi")
  private final WebClient openapi;


  // 가장 최신의 지수 정보를 로드해 저장합니다.
  public List<SyncJobDto> syncIndexInfos(HttpServletRequest request) {

    List<SyncJob> response = new ArrayList<>();
    String requestIpAddr = request.getRemoteAddr();
    LocalDate baseDate = getLastWeekday();

    // DB에서 지수 정보 전체를 map으로 미리 불러오기 (key는 unique 체크)
    Map<String, IndexInfo> indexInfoMap = indexInfoRepository.findAll()
        .stream()
        .collect(Collectors.toMap(
            idxInfo -> idxInfo.getIndexClassification() + "_" + idxInfo.getIndexName(),
            Function.identity()
        ));

    int pageNo = 1;

    while (true) {
      StockMarketIndexResponse openApiResponse = fetchToOpenApi(pageNo, baseDate);

      List<StockIndexDto> stockIndexDtoList = extractDtoListFromResponse(openApiResponse);
      if (stockIndexDtoList == null || stockIndexDtoList.isEmpty()) {
        break;
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

  private StockMarketIndexResponse fetchToOpenApi(int pageNo, LocalDate baseDate) {
    try {
      return openapi.get()
          .uri(uriBuilder -> uriBuilder
              .queryParam("pageNo", pageNo)
              .queryParam("numOfRows", 50)
              .queryParam("basDt", baseDate.format(DateTimeFormatter.BASIC_ISO_DATE))
              .build()
          )
          .retrieve()
          .bodyToMono(StockMarketIndexResponse.class)
          .block(Duration.ofSeconds(5));

    } catch (Exception e) {
      throw new BusinessLogicException(ExceptionCode.OPEN_API_REQUEST_FAILED, e.getMessage());
    }
  }

  private List<StockIndexDto> extractDtoListFromResponse(StockMarketIndexResponse response) {
    // 응답 형태가 맞지 않은 경우
    if (response == null ||
        response.response() == null ||
        response.response().body() == null ||
        response.response().body().items() == null ||
        response.response().body().items().item() == null) {
      return null;
    }

    // 에러코드가 온 경우
    if (!response.response().header().resultCode().equals("00")) {
      return null;
    }

    return response.response().body().items().item();
  }

  // 오늘 이전의 가장 마지막 평일 구하기
  private LocalDate getLastWeekday() {
    LocalDate yesterday = LocalDate.now().minusDays(1);

    LocalDate lastWeekday = yesterday;
    if (yesterday.getDayOfWeek() == DayOfWeek.SATURDAY) {
      lastWeekday = yesterday.minusDays(1);
    } else if (yesterday.getDayOfWeek() == DayOfWeek.SUNDAY) {
      lastWeekday = yesterday.minusDays(2);
    }

    return lastWeekday;
  }
}
