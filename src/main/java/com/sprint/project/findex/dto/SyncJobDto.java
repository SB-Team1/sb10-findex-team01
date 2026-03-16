package com.sprint.project.findex.dto;

import com.sprint.project.findex.global.entity.JobType;
import com.sprint.project.findex.global.entity.ResultType;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SyncJobDto {

  private Long id;
  private JobType jobType;
  private Long indexInfoId;
  private Instant targetDate;
  private String worker;
  private Instant jobTime;
  private ResultType result;
}
