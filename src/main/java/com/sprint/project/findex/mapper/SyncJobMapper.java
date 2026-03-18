package com.sprint.project.findex.mapper;

import com.sprint.project.findex.dto.syncjob.SyncJobDto;
import com.sprint.project.findex.entity.SyncJob;
import com.sprint.project.findex.mapper.config.GlobalMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class)
public interface SyncJobMapper extends BaseMapper<SyncJob> {

  @Mapping(source = "indexInfo.id", target = "indexInfoId")
  SyncJobDto toDto(SyncJob syncJob);
}
