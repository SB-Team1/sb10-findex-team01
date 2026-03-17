package com.sprint.project.findex.entity;

import com.sprint.project.findex.dto.indexdata.IndexDataUpdateRequest;
import com.sprint.project.findex.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigInteger;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Entity
@Table(name = "index_datas")
public class IndexData extends BaseEntity {

  //지수 정보
  @Setter
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
  private Long tradingQuantity;
  //지수에 포함된 종목의 거래대금 총합
  @Column(nullable = false)
  private BigInteger tradingPrice;
  //지수에 포함된 종목의 시가 총액
  @Column(nullable = false)
  private BigInteger marketTotalAmount;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private DeletedStatus isDeleted;

  @Builder
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
      Long tradingQuantity,
      BigInteger tradingPrice,
      BigInteger marketTotalAmount,
      DeletedStatus isDeleted
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
    this.isDeleted = isDeleted;
  }

  public void update(IndexDataUpdateRequest request) {
    updateIfChanged(this.marketPrice, request.marketPrice(), val -> this.marketPrice = val);
    updateIfChanged(this.closingPrice, request.closingPrice(), val -> this.closingPrice = val);
    updateIfChanged(this.highPrice, request.highPrice(), val -> this.highPrice = val);
    updateIfChanged(this.lowPrice, request.lowPrice(), val -> this.lowPrice = val);
    updateIfChanged(this.versus, request.versus(), val -> this.versus = val);
    updateIfChanged(this.fluctuationRate, request.fluctuationRate(), val -> this.fluctuationRate = val);
    updateIfChanged(this.tradingQuantity, request.tradingQuantity(), val -> this.tradingQuantity = val);
    updateIfChanged(this.tradingPrice, request.tradingPrice(), val -> this.tradingPrice = val);
    updateIfChanged(this.marketTotalAmount, request.marketTotalAmount(), val -> this.marketTotalAmount = val);
    this.sourceType = SourceType.USER;
  }

  public void updateIsDeleted(DeletedStatus isDeleted) {
    this.isDeleted = isDeleted;
  }
}
