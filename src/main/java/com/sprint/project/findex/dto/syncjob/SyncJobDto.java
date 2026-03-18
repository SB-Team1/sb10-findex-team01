package com.sprint.project.findex.dto.syncjob;

import com.sprint.project.findex.global.entity.JobType;
import com.sprint.project.findex.global.entity.ResultType;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
  private LocalDate targetDate;
  private String worker;
  private LocalDateTime jobTime;
  private ResultType result;
}
