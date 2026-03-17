package com.sprint.project.findex.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SortDirection {
  ASC, DESC;

  // todo: exception
  @JsonCreator
  public static SortDirection from(String value) {
    try {
      return SortDirection.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    }
  }
}
