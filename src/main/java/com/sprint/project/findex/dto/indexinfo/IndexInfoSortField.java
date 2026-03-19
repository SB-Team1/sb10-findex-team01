package com.sprint.project.findex.dto.indexinfo;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IndexInfoSortField {
  INDEX_CLASSIFICATION("indexClassification", IndexInfoDto::indexClassification),
  INDEX_NAME("indexName", IndexInfoDto::indexName),
  EMPLOYED_ITEMS_COUNT("employedItemsCount", IndexInfoDto::employedItemsCount);

  private final String name;
  private final Function<IndexInfoDto, Object> getter;

  // todo: custom exception
  @JsonCreator
  public static IndexInfoSortField from(String value) {
    for (IndexInfoSortField sortField : IndexInfoSortField.values()) {
      if (sortField.getName().equals(value)) {
        return sortField;
      }
    }
    throw new IllegalArgumentException("incorrect value, %s".formatted(value));
  }
}
