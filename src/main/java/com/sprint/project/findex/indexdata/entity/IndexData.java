package com.sprint.project.findex.indexdata.entity;

import com.sprint.project.findex.global.entity.DeletedStatus;
import com.sprint.project.findex.global.entity.SourceType;
import com.sprint.project.findex.IndexInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Entity
@Table(name = "index_datas")
public class IndexData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  private Instant updatedAt;
  //지수 정보
  @ManyToOne
  @JoinColumn(name = "index_info_id")
  private IndexInfo indexInfo;
  //기준 일자
  @Column(nullable = false)
  private LocalDate baseDate;
  //정보가 입력된 출처
  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private SourceType sourceType;
  //정규시장의 매매시간 개시 후 형성되는 최초 가격
  @Column(nullable = false)
  private Double marketPrice;
  //정규시장의 매매시간 종료시까지 형성되는 최종 가격
  @Column(nullable = false)
  private Double closingPrice;
  //하루 중 지수의 최고치
  @Column(nullable = false)
  private Double highPrice;
  //하루 중 지수의 최저치
  @Column(nullable = false)
  private Double lowPrice;
  //전일 대비 등락
  @Column(nullable = false)
  private Double versus;
  //전일 대비 등락에 따른 비율
  @Column(nullable = false)
  private Double fluctuationRate;
  //지수에 포함된 종목의 거래량 총합
  @Column(nullable = false)
  private Double tradingQuantity;
  //지수에 포함된 종목의 거래대금 총합
  @Column(nullable = false)
  private Long tradingPrice;
  //지수에 포함된 종목의 시가 총액
  @Column(nullable = false)
  private Long marketTotalAmount;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private DeletedStatus isDeleted = DeletedStatus.ACTIVE;

  public IndexData(
      IndexInfo indexInfo,
      LocalDate baseDate,
      SourceType sourceType,
      Double marketPrice,
      Double closingPrice,
      Double highPrice,
      Double lowPrice,
      Double versus,
      Double fluctuationRate,
      Double tradingQuantity,
      Long tradingPrice,
      Long marketTotalAmount
  ) {
    this.indexInfo = indexInfo;
    this.baseDate = baseDate;
    this.sourceType = sourceType;
    this.marketPrice = marketPrice;
    this.closingPrice = closingPrice;
    this.highPrice = highPrice;
    this.lowPrice = lowPrice;
    this.versus = versus;
    this.fluctuationRate = fluctuationRate;
    this.tradingQuantity = tradingQuantity;
    this.tradingPrice = tradingPrice;
    this.marketTotalAmount = marketTotalAmount;
  }

  public void updateSourceType(SourceType sourceType) {
    this.sourceType = sourceType;
  }

  public void updateMarketPrice(Double marketPrice) {
    this.marketPrice = marketPrice;
  }

  public void updateClosingPrice(Double closingPrice) {
    this.closingPrice = closingPrice;
  }

  public void updateHighPrice(Double highPrice) {
    this.highPrice = highPrice;
  }

  public void updateLowPrice(Double lowPrice) {
    this.lowPrice = lowPrice;
  }

  public void updateVersus(Double versus) {
    this.versus = versus;
  }

  public void updateFluctuationRate(Double fluctuationRate) {
    this.fluctuationRate = fluctuationRate;
  }

  public void updateTradingQuantity(Double tradingQuantity) {
    this.tradingQuantity = tradingQuantity;
  }

  public void updateTradingPrice(Long tradingPrice) {
    this.tradingPrice = tradingPrice;
  }

  public void updateMarketTotalAmount(Long marketTotalAmount) {
    this.marketTotalAmount = marketTotalAmount;
  }

  public void updateIsDeleted(DeletedStatus isDeleted) {
    this.isDeleted = isDeleted;
  }
}
