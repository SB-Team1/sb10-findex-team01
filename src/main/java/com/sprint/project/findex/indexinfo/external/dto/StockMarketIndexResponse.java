package com.sprint.project.findex.indexinfo.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public record StockMarketIndexResponse(@JsonProperty("header") OpenAPIHeaderDto header,
                                       @JsonProperty("body") BodyStockMarketIndexDto bodyDto) {

  public record OpenAPIHeaderDto(@NotNull String resultCode, @NotNull String resultMsg) {

  }

  public record BodyStockMarketIndexDto(@NotNull int numOfRows, @NotNull int pageNo,
                                        @NotNull int totalCount,
                                        @NotNull StockMarketIndexResponse.RawStockMarketIndexDto items) {

  }

  public record RawStockMarketIndexDto(List<IndexDto> item) {

  }

  public record IndexDto(@JsonProperty("basDt") @NotNull LocalDate baseDate,
                         @JsonProperty("idxNm") @NotNull String indexName,
                         @JsonProperty("idxCsf") @NotNull String indexClassification,
                         @JsonProperty("epyItmsCnt") @NotNull Long employedItemsCount,
                         @JsonProperty("clpr") @NotNull Double closingPrice,
                         @JsonProperty("vs") @NotNull Double versus,
                         @JsonProperty("fltRt") @NotNull Double fluctuationRate,
                         @JsonProperty("mkp") @NotNull Double marketPrice,
                         @JsonProperty("hipr") @NotNull Double highPrice,
                         @JsonProperty("lopr") @NotNull Double lowPrice,
                         @JsonProperty("trqu") @NotNull Long tradingQuantity,
                         @JsonProperty("trPrc") @NotNull BigInteger tradingPrice,
                         @JsonProperty("basPntm") @NotNull LocalDate basePointInTime,
                         @JsonProperty("lstgMrktTotAmt") @NotNull BigInteger marketTotalAmount,
                         @JsonProperty("basIdx") @NotNull Double baseIndex,
                         Long lsYrEdVsFltRg,
                         Double lsYrEdVsFltRt,
                         Double yrWRcrdHgst,
                         LocalDate yrWRcrdHgstDt,
                         Long yrWRcrdLwst,
                         LocalDate yrWRcrdLwstDt) {

  }
}
