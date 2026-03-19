package com.sprint.project.findex.dto.openapi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public record StockMarketIndexResponse(ResponseWrapper response) {

  public record ResponseWrapper(OpenAPIHeaderDto header, BodyStockMarketIndexDto body) {

  }

  public record OpenAPIHeaderDto(@NotNull String resultCode, @NotNull String resultMsg) {

  }

  public record BodyStockMarketIndexDto(@NotNull int numOfRows, @NotNull int pageNo,
                                        @NotNull int totalCount,
                                        @NotNull RawStockMarketIndexDto items) {

  }

  public record RawStockMarketIndexDto(List<StockIndexDto> item) {

  }

  public record StockIndexDto(
      @JsonProperty("basDt") @JsonFormat(shape = Shape.STRING, pattern = "yyyyMMdd")
      @NotNull
      LocalDate baseDate,
      @JsonProperty("basPntm") @JsonFormat(shape = Shape.STRING, pattern = "yyyyMMdd")
      @NotNull
      LocalDate basePointInTime,
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
      @JsonProperty("lstgMrktTotAmt") @NotNull BigInteger marketTotalAmount,
      @JsonProperty("basIdx") @NotNull Double baseIndex,
      Long lsYrEdVsFltRg,
      Double lsYrEdVsFltRt,
      Double yrWRcrdHgst,
      String yrWRcrdHgstDt,
      Double yrWRcrdLwst,
      String yrWRcrdLwstDt) {

  }
}
