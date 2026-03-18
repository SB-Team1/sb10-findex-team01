package com.sprint.project.findex.service.openapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.project.findex.dto.openapi.StockMarketIndexRequest;
import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse;
import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse.StockIndexDto;
import com.sprint.project.findex.entity.AutoSyncConfig;
import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.service.openapi.internal.PersistentWorker;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class OpenApiService {

  private final WebClient openapi;
  private final ObjectMapper objectMapper;
  private final PersistentWorker worker;

  public void fetchAndSave() {
    int pageNo = 1;
    int numOfRows = 500;
    while (true) {
      StockMarketIndexRequest request = getRequest(pageNo, numOfRows);
      StockMarketIndexResponse result = fetch(request).block();
      int totalCount = getTotalCount(result);
      if (!hasNext(pageNo, numOfRows, totalCount)) {
        break;
      }
      List<StockIndexDto> stockIndexDtos = getRawIndexDto(result);
      worker.save(stockIndexDtos);
      pageNo++;
    }
  }

  public void fetchAndSaveByAutoSync(List<AutoSyncConfig> enabledConfigs) {
    DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;

    Map<Long, LocalDate> lastSyncDateMap = worker.findLastSyncDatesBulk(enabledConfigs);

    for (AutoSyncConfig config : enabledConfigs) {
      IndexInfo indexInfo = config.getIndexInfo();
      String indexName = indexInfo.getIndexName();

      LocalDate lastSyncDate = lastSyncDateMap.get(indexInfo.getId());
      LocalDate beginDate = lastSyncDate.plusDays(1);
      LocalDate endDate = LocalDate.now();

      int pageNo = 1;
      int numOfRows = 1000;
      List<StockIndexDto> buffer = new ArrayList<>();

      while (true) {
        StockMarketIndexRequest request = StockMarketIndexRequest.builder()
            .pageNo(pageNo)
            .numOfRows(numOfRows)
            .beginBaseDate(beginDate.format(formatter))
            .endBaseDate(endDate.format(formatter))
            .indexName(URLEncoder.encode(indexName, StandardCharsets.UTF_8))
            .build();

        StockMarketIndexResponse result = fetch(request).block();
        if (result == null || result.response() == null || result.response().body() == null) break;

        List<StockIndexDto> dtos = getRawIndexDto(result);
        if (dtos.isEmpty()) break;

        buffer.addAll(dtos);

        while (buffer.size() >= PersistentWorker.CHUNK_SIZE) {
          List<StockIndexDto> subChunk = new ArrayList<>(buffer.subList(0, PersistentWorker.CHUNK_SIZE));
          worker.saveIndexDataBatch(subChunk, indexInfo);
          buffer.subList(0, PersistentWorker.CHUNK_SIZE).clear();
        }

        int totalCount = getTotalCount(result);
        if (!hasNext(pageNo, numOfRows, totalCount)) break;
        pageNo++;
      }

      if (!buffer.isEmpty()) {
        worker.saveIndexDataBatch(buffer, indexInfo);
      }
    }
  }

  private boolean hasNext(int pageNo, int numOfRows, int totalCount) {
    return pageNo * numOfRows <= totalCount;
  }

  private List<StockIndexDto> getRawIndexDto(StockMarketIndexResponse result) {
    return result.response().body().items().item();
  }

  private int getTotalCount(StockMarketIndexResponse result) {
    return result.response().body().totalCount();
  }

  private Mono<StockMarketIndexResponse> fetch(StockMarketIndexRequest request) {
    Map<String, Object> queryParams = objectMapper.convertValue(request, new TypeReference<>() {
    });
    return openapi.get()
        .uri(uriBuilder -> {
          queryParams.forEach(uriBuilder::queryParam);
          return uriBuilder.build();
        })
        .retrieve()
        .bodyToMono(StockMarketIndexResponse.class);
  }

  private StockMarketIndexRequest getRequest(int pageNo, int numOfRows) {
    return StockMarketIndexRequest.builder()
        .pageNo(pageNo)
        .numOfRows(numOfRows)
        .build();
  }

  private LocalDate getLastAutoSyncDate(AutoSyncConfig config) {
    return worker.findLastSyncDate(config);
  }
}
