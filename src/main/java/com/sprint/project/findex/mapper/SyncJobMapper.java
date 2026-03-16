package com.sprint.project.findex.mapper;

import com.sprint.project.findex.dto.SyncJobDto;
import com.sprint.project.findex.entity.SyncJob;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SyncJobMapper {

  @Mapping(source = "indexInfo.id", target = "indexInfoId")
  public SyncJobDto toDto(SyncJob syncJob);
}
