package com.sprint.project.findex.dto.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonInclude(Include.NON_NULL)
public record StockMarketIndexRequest(
    String serviceKey,
    String resultType,
    int pageNo,
    int numOfRows,
    @JsonProperty("basDt") String baseDate,
    @JsonProperty("beginBasDt") String beginBaseDate,
    @JsonProperty("endBasDt") String endBaseDate,
    @JsonProperty("likeBasDt") String likeBaseDate,
    @JsonProperty("idxNm") String indexName,
    @JsonProperty("likeIdxNm") String likeIndexName,
    @JsonProperty("beginEpyItmsCnt") Long beginEmployedItemsCount,
    @JsonProperty("endEpyItmsCnt") Long endEmployedItemsCount,
    @JsonProperty("beginFltRt") String beginFluctuationRate,
    @JsonProperty("endFltRt") String endFluctuationRate,
    @JsonProperty("beginTrqu") String beginTradingQuantity,
    @JsonProperty("endTrqu") String endTradingQuantity,
    @JsonProperty("beginTrPrc") String beginTradingPrice,
    @JsonProperty("endTrPrc") String endTradingPrice,
    @JsonProperty("beginLstgMrktTotAmt") String beginListingMarketTotalAmount,
    @JsonProperty("endLstgMrktTotAmt") String endListingMarketTotalAmount,
    @JsonProperty("beginLsYrEdVsFltRg") String beginLastYearEndVersusFluctuationRange,
    @JsonProperty("endLsYrEdVsFltRg") String endLastYearEndVersusFluctuationRange,
    @JsonProperty("beginLsYrEdVsFltRt") String beginLastYearEndVersusFluctuationRate,
    @JsonProperty("endLsYrEdVsFltRt") String endLastYearEndVersusFluctuationRate) {

  public StockMarketIndexRequest {
    if (resultType == null || resultType.isEmpty()) {
      resultType = "json";
    }
    if (pageNo == 0) {
      pageNo = 1;
    }
    if (numOfRows == 0) {
      numOfRows = 10;
    }
  }

}
