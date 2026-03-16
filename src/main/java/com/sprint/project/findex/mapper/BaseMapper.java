package com.sprint.project.findex.mapper;

import com.sprint.project.findex.dto.openapi.StockMarketIndexResponse.StockIndexDto;
import java.util.List;

public interface BaseMapper<T> {

  T toEntity(StockIndexDto dto);

  List<T> toEntities(List<StockIndexDto> dtos);
}
