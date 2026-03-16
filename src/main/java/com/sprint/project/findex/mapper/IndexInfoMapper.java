package com.sprint.project.findex.mapper;

import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse.StockIndexDto;
import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.mapper.config.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class)
public interface IndexInfoMapper extends BaseMapper<IndexInfo> {

  @Override
  @Mapping(target = "sourceType", constant = "OPEN_API")
  @Mapping(target = "isDeleted", constant = "ACTIVE")
  IndexInfo toEntity(StockIndexDto rawDto);
}
