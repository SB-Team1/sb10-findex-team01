package com.sprint.project.findex.autosyncconfig.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
  Instant timestampExpand;
  int statusExpand;
  String messageExpand;
  String detailsExpand;
}
