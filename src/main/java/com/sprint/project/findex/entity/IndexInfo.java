package com.sprint.project.findex.entity;

import com.sprint.project.findex.dto.IndexInfoDto;
import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse;
import com.sprint.project.findex.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private SourceType sourceType;

  @Column
  private boolean favorite;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  private DeletedStatus isDeleted;

  @Builder
  public IndexInfo(
      String indexClassification,
      String indexName,
      Long employedItemsCount,
      LocalDate basePointInTime,
      Double baseIndex,
      SourceType sourceType,
      boolean favorite,
      DeletedStatus isDeleted) {
    this.indexClassification = indexClassification;
    this.indexName = indexName;
    this.employedItemsCount = employedItemsCount;
    this.basePointInTime = basePointInTime;
    this.baseIndex = baseIndex;
    this.sourceType = sourceType;
    this.favorite = favorite;
    this.isDeleted = isDeleted;
  }

  // todo
  public void update(IndexInfoDto dto) {
  }


  public void updateByOpenAPI(StockMarketIndexResponse.StockIndexDto stockIndexDto) {
    this.indexClassification = stockIndexDto.indexClassification();
    this.indexName = stockIndexDto.indexName();
    this.employedItemsCount = stockIndexDto.employedItemsCount();
    this.basePointInTime = stockIndexDto.basePointInTime();
    this.sourceType = SourceType.OPEN_API;
  }

  @Override
  public String toString() {
    return "IndexInfo{" +
        "indexClassification='" + indexClassification + '\'' +
        ", indexName='" + indexName + '\'' +
        ", employedItemsCount=" + employedItemsCount +
        ", basePointInTime=" + basePointInTime +
        ", baseIndex=" + baseIndex +
        ", sourceType=" + sourceType +
        ", favorite=" + favorite +
        ", isDeleted=" + isDeleted +
        '}';
  }
}
