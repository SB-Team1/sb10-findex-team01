package com.sprint.project.findex.dto.indexdata;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IndexDataCsvHeader {
  BASE_DATE("기준일자"),
  MARKET_PRICE("시가"),
  CLOSING_PRICE("종가"),
  HIGH_PRICE("고가"),
  LOW_PRICE("저가"),
  VERSUS("전일대비등락"),
  FLUCTUATION_RATE("등락률"),
  TRADING_QUANTITY("거래량"),
  TRADING_PRICE("거래대금"),
  MARKET_TOTAL_AMOUNT("시가총액");

  private final String description;

  public static String[] getHeaderArray() {
    return Arrays.stream(values())
        .map(IndexDataCsvHeader::getDescription)
        .toArray(String[]::new);
  }
}
