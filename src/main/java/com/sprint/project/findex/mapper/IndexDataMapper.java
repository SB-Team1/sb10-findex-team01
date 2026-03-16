package com.sprint.project.findex.mapper;

import com.sprint.project.findex.dto.IndexDataDto;
import com.sprint.project.findex.entity.IndexData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndexDataMapper {
  @Mapping(source = "indexInfo.id", target = "indexInfoId")
  IndexDataDto toDto(IndexData indexData);
}
