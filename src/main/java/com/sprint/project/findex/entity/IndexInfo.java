package com.sprint.project.findex.entity;

import com.sprint.project.findex.dto.indexinfo.IndexInfoUpdateRequest;
import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse;
import com.sprint.project.findex.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "index_infos")
public class IndexInfo extends BaseEntity {

  @Column(nullable = false, length = 240)
  private String indexClassification;

  @Column(nullable = false, length = 240)
  private String indexName;

  @Column(nullable = false)
  private Long employedItemsCount;

  @Column(nullable = false)
  private LocalDate basePointInTime;

  @Column(nullable = false)
  private Double baseIndex;

  @Builder.Default
  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private SourceType sourceType = SourceType.USER;

  @Column
  private boolean favorite;

  public void update(IndexInfoUpdateRequest request) {
    updateIfChanged(employedItemsCount, request.employedItemsCount(),
        val -> employedItemsCount = val);
    updateIfChanged(basePointInTime, request.basePointInTime(), val -> basePointInTime = val);
    updateIfChanged(baseIndex, request.baseIndex(), val -> baseIndex = val);
    updateIfChanged(favorite, request.favorite(), val -> favorite = val);
  }

  public void updateByOpenAPI(StockMarketIndexResponse.StockIndexDto stockIndexDto) {
    this.indexClassification = stockIndexDto.indexClassification();
    this.indexName = stockIndexDto.indexName();
    this.employedItemsCount = stockIndexDto.employedItemsCount();
    this.basePointInTime = stockIndexDto.basePointInTime();
    this.sourceType = SourceType.OPEN_API;
  }
}
