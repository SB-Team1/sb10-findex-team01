package com.sprint.project.findex.mapper;

import com.sprint.project.findex.dto.indexdata.IndexDataDto;
import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse.StockIndexDto;
import com.sprint.project.findex.entity.IndexData;
import com.sprint.project.findex.mapper.config.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class)
public interface IndexDataMapper extends BaseMapper<IndexData> {

  @Mapping(source = "indexInfo.id", target = "indexInfoId")
  IndexDataDto toDto(IndexData indexData);

  @Override
  @Mapping(target = "sourceType", constant = "OPEN_API")
  @Mapping(target = "isDeleted", constant = "ACTIVE")
  IndexData toEntity(StockIndexDto stockIndexDto);
}
