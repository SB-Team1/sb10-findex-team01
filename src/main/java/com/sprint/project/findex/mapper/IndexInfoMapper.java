package com.sprint.project.findex.mapper;

import com.sprint.project.findex.dto.indexinfo.IndexInfoCreateRequest;
import com.sprint.project.findex.dto.indexinfo.IndexInfoDto;
import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse.StockIndexDto;
import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.mapper.config.GlobalMapperConfig;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class)
public interface IndexInfoMapper extends BaseMapper<IndexInfo> {

  IndexInfoDto toDto(IndexInfo indexInfo);

  List<IndexInfoDto> toDtoList(List<IndexInfo> indexInfos);

  IndexInfo toEntity(IndexInfoCreateRequest request);

  @Override
  @Mapping(target = "sourceType", constant = "OPEN_API")
  IndexInfo toEntity(StockIndexDto rawDto);
}
