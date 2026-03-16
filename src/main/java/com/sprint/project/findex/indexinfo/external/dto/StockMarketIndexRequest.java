package com.sprint.project.findex.indexinfo.external.dto;

import jakarta.validation.constraints.NotNull;

public record StockMarketIndexRequest(
    @NotNull String serviceKey, @NotNull String resultType, int pageNo, int numOfRows,
    String basDt, String beginBasDt, String endBasDt, String likeBasDt,
    String idxNm, String likeIdxNm, String beginEpyItmsCnt, String endEpyItmsCnt,
    String beginFltRt, String endFltRt, String beginTrqu, String endTrqu,
    String beginTrPrc, String endTrPrc, String beginLstgMrktTotAmt, String endLstgMrktTotAmt,
    String beginLsYrEdVsFltRg, String endLsYrEdVsFltRg, String beginLsYrEdVsFltRt,
    String endLsYrEdVsFltRt) {

}
