package com.sprint.project.findex.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.querydsl.core.types.Order;

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

  public static Order toOrderType(String value) {
    try {
      return Order.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    }
  }
}
