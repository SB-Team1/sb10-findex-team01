package com.sprint.project.findex.service.openapi.internal;

import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse.StockIndexDto;
import com.sprint.project.findex.entity.AutoSyncConfig;
import com.sprint.project.findex.entity.IndexData;
import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.entity.SourceType;
import com.sprint.project.findex.entity.SyncJob;
import com.sprint.project.findex.global.entity.JobType;
import com.sprint.project.findex.global.entity.ResultType;
import com.sprint.project.findex.mapper.IndexDataMapper;
import com.sprint.project.findex.mapper.IndexInfoMapper;
import com.sprint.project.findex.repository.indexdata.IndexDataRepository;
import com.sprint.project.findex.repository.indexinfo.IndexInfoRepository;
import com.sprint.project.findex.repository.syncjob.SyncJobRepository;
import com.sprint.project.findex.service.AutoSyncConfigService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class PersistentWorker {

  public static final int CHUNK_SIZE = 5000;
  private final IndexInfoMapper indexInfoMapper;
  private final IndexDataMapper indexDataMapper;
  private final AutoSyncConfigService autoSyncConfigService;
  private final IndexInfoRepository indexInfoRepository;
  private final IndexDataRepository indexDataRepository;
  private final SyncJobRepository syncJobRepository;
  private final JdbcTemplate jdbcTemplate;

  @Transactional
  public void save(List<StockIndexDto> stockIndexDtos) {
    List<IndexInfo> indexInfos = indexInfoMapper.toEntities(stockIndexDtos);
    indexInfoRepository.saveAll(indexInfos);
    List<IndexData> indexDatas = indexDataMapper.toEntities(stockIndexDtos);
    for (int i = 0; i < indexDatas.size(); i++) {
      indexDatas.get(i).setIndexInfo(indexInfos.get(i));
    }
    indexDataRepository.saveAll(indexDatas);
  }

  @Transactional
  public List<SyncJob> saveIndexInfoAndSyncJob(
      List<StockIndexDto> dtos,
      Map<String, IndexInfo> indexInfoMap,
      String requestIpAddr
  ) {

    List<IndexInfo> toInsert = new ArrayList<>();
    List<SyncJob> syncJobs = new ArrayList<>();

    for (StockIndexDto dto : dtos) {
      String key = createIndexInfoKey(dto.indexName(), dto.indexClassification());
      IndexInfo indexInfo = indexInfoMap.get(key);

      if (indexInfo != null) {
        indexInfo.updateByOpenAPI(dto);
      } else {
        indexInfo = IndexInfo.builder()
            .indexClassification(dto.indexClassification())
            .indexName(dto.indexName())
            .employedItemsCount(dto.employedItemsCount())
            .basePointInTime(dto.basePointInTime())
            .baseIndex(dto.baseIndex())
            .sourceType(SourceType.OPEN_API)
            .favorite(false)
            .build();
        toInsert.add(indexInfo);
        indexInfoMap.put(key, indexInfo);
      }

      syncJobs.add(
          new SyncJob(indexInfo, JobType.INDEX_INFO, null, requestIpAddr, ResultType.SUCCESS)
      );
    }

    indexInfoRepository.saveAll(toInsert);
    syncJobRepository.saveAll(syncJobs);
    autoSyncConfigService.createAll(toInsert);

    return syncJobs;
  }

  @Transactional
  public void saveIndexDataBatch(List<StockIndexDto> dtos, IndexInfo indexInfo) {
    if (dtos.isEmpty()) {
      return;
    }

    String sql = """
        INSERT INTO index_datas
        (base_date, closing_price, fluctuation_rate, high_price,
        index_info_id, low_price, market_price, market_total_amount,
        source_type, trading_price, trading_quantity, versus, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
        ON CONFLICT (index_info_id, base_date)
        DO NOTHING
        """;

    int total = dtos.size();
    for (int from = 0; from < total; from += CHUNK_SIZE) {
      int to = Math.min(from + CHUNK_SIZE, total);
      List<StockIndexDto> chunk = dtos.subList(from, to);

      jdbcTemplate.batchUpdate(sql, chunk, chunk.size(), (ps, dto) -> {
        ps.setObject(1, dto.baseDate());
        ps.setDouble(2, dto.closingPrice());
        ps.setDouble(3, dto.fluctuationRate());
        ps.setDouble(4, dto.highPrice());
        ps.setLong(5, indexInfo.getId());
        ps.setDouble(6, dto.lowPrice());
        ps.setDouble(7, dto.marketPrice());
        ps.setBigDecimal(8, new BigDecimal(dto.marketTotalAmount().toString()));
        ps.setString(9, "OPEN_API");
        ps.setBigDecimal(10, new BigDecimal(dto.tradingPrice().toString()));
        ps.setLong(11, dto.tradingQuantity());
        ps.setDouble(12, dto.versus());
      });
    }
  }

  public Map<Long, LocalDate> findLastSyncDatesBulk(List<AutoSyncConfig> configs) {
    if (configs.isEmpty()) {
      return new HashMap<>();
    }

    List<SyncJobRepository.LastSyncDateProjection> results =
        syncJobRepository.findLastSyncDatesEnabledOnly();

    Map<Long, LocalDate> lastSyncMap = new HashMap<>();

    for (SyncJobRepository.LastSyncDateProjection res : results) {
      if (res.getIndexInfoId() != null) {
        lastSyncMap.put(res.getIndexInfoId(), res.getLastDate());
      }
    }

    for (AutoSyncConfig config : configs) {
      lastSyncMap.putIfAbsent(config.getIndexInfo().getId(),
          config.getIndexInfo().getBasePointInTime());
    }

    return lastSyncMap;
  }

  public LocalDate findLastSyncDate(AutoSyncConfig config) {
    return indexDataRepository.findTopByIndexInfoOrderByBaseDateDesc(config.getIndexInfo())
        .map(IndexData::getBaseDate)
        .orElseGet(() -> config.getIndexInfo().getCreatedAt()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        );
  }

  private String createIndexInfoKey(String indexName, String indexClassification) {
    return indexName + "_" + indexClassification;
  }
}
