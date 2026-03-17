package com.sprint.project.findex.dto.indexdata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.sprint.project.findex.entity.IndexData;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum IndexDataSortField {
  MARKET_PRICE("marketPrice", Double.class, Double::valueOf, IndexData::getMarketPrice),
  CLOSING_PRICE("closingPrice", Double.class, Double::valueOf, IndexData::getClosingPrice),
  HIGH_PRICE("highPrice", Double.class, Double::valueOf, IndexData::getHighPrice),
  LOW_PRICE("lowPrice", Double.class, Double::valueOf, IndexData::getLowPrice),
  VERSUS("versus", Double.class, Double::valueOf, IndexData::getVersus),
  FLUCTUATION_RATE("fluctuationRate", Double.class, Double::valueOf, IndexData::getFluctuationRate),
  TRADING_QUANTITY("tradingQuantity", Long.class, Long::valueOf, IndexData::getTradingQuantity),
  TRADING_PRICE("tradingPrice", BigInteger.class, BigInteger::new, IndexData::getTradingPrice),
  MARKET_TOTAL_AMOUNT("marketTotalAmount", BigInteger.class, BigInteger::new, IndexData::getMarketTotalAmount),
  BASE_DATE("baseDate", LocalDate.class, LocalDate::parse, IndexData::getBaseDate);

  private final String name;
  private final Class<? extends Comparable<?>> type;
  private final Function<String, ? extends Comparable<?>> parser;
  private final Function<IndexData, ? extends Comparable<?>> extractor;

  public Comparable<?> parseCursor(String cursorValue) {
    return this.parser.apply(cursorValue);
  }

  public String extractValueToString(IndexData indexData) {
    return String.valueOf(this.extractor.apply(indexData));
  }

  // todo: custom exception
  @JsonCreator
  public static IndexDataSortField from(String value) {
    for (IndexDataSortField sortField : IndexDataSortField.values()) {
      if (sortField.getName().equals(value)) {
        return sortField;
      }
    }
    throw new IllegalArgumentException("incorrect value, %s".formatted(value));
  }
}
