package com.sprint.project.findex.dto.indexinfo;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum IndexInfoSortField {
  INDEX_CLASSIFICATION("indexClassification"),
  INDEX_NAME("indexName"),
  EMPLOYED_ITEMS_COUNT("employedItemsCount");

  @Getter
  private final String name;

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
