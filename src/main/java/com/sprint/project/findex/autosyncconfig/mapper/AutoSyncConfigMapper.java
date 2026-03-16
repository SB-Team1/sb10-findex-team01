package com.sprint.project.findex.autosyncconfig.mapper;

import com.sprint.project.findex.autosyncconfig.dto.AutoSyncConfigDto;
import com.sprint.project.findex.autosyncconfig.entity.AutoSyncConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AutoSyncConfigMapper {

  @Mapping(source = "indexInfo.id", target = "id")
  @Mapping(source = "indexInfo.indexName", target = "indexName")
  @Mapping(source = "indexInfo.classification", target = "indexClassification")
  AutoSyncConfigDto toDto(AutoSyncConfig autoSyncConfig);
}
