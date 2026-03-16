package com.sprint.project.findex.service.openapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.project.findex.dto.openapi.StockMarketIndexRequest;
import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse;
import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse.StockIndexDto;
import com.sprint.project.findex.service.openapi.internal.PersistentWorker;
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
}
