package com.sprint.project.findex.service.openapi.internal;

import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse.StockIndexDto;
import com.sprint.project.findex.entity.DeletedStatus;
import com.sprint.project.findex.entity.IndexData;
import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.entity.SourceType;
import com.sprint.project.findex.entity.SyncJob;
import com.sprint.project.findex.global.entity.JobType;
import com.sprint.project.findex.global.entity.ResultType;
import com.sprint.project.findex.mapper.IndexDataMapper;
import com.sprint.project.findex.mapper.IndexInfoMapper;
import com.sprint.project.findex.repository.IndexDataRepository;
import com.sprint.project.findex.repository.IndexInfoRepository;
import com.sprint.project.findex.repository.SyncJobRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class PersistentWorker {

  private final IndexInfoMapper indexInfoMapper;
  private final IndexDataMapper indexDataMapper;
  private final IndexInfoRepository indexInfoRepository;
  private final IndexDataRepository indexDataRepository;
  private final SyncJobRepository syncJobRepository;

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
      String key = dto.indexClassification() + "_" + dto.indexName();

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
            .isDeleted(DeletedStatus.ACTIVE)
            .build();
        toInsert.add(indexInfo);
        indexInfoMap.put(key, indexInfo);
      }

      syncJobs.add(
          new SyncJob(indexInfo, JobType.INDEX_INFO, null, requestIpAddr, ResultType.SUCCESS)
      );
    }

    indexInfoRepository.saveAll(toInsert);
    indexInfoRepository.flush();

    syncJobRepository.saveAll(syncJobs);

    return syncJobs;
  }
}
