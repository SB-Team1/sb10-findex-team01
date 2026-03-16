package com.sprint.project.findex.service.openapi.internal;

import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse.StockIndexDto;
import com.sprint.project.findex.entity.IndexData;
import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.mapper.IndexDataMapper;
import com.sprint.project.findex.mapper.IndexInfoMapper;
import com.sprint.project.findex.repository.IndexDataRepository;
import com.sprint.project.findex.repository.IndexInfoRepository;
import java.util.List;
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
}
