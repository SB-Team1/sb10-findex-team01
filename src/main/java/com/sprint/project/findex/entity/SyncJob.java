package com.sprint.project.findex.entity;

import com.sprint.project.findex.global.entity.JobType;
import com.sprint.project.findex.global.entity.ResultType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "sync_jobs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class SyncJob {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 연동한 지수 정보
  @ManyToOne
  @JoinColumn(name = "index_info_id")
  private IndexInfo indexInfo;

  // 연동 대상
  @Column(length = 10, nullable = false)
  @Enumerated(EnumType.STRING)
  private JobType jobType;

  // 지수 데이터 연동 대상 날짜
  @Column
  private LocalDate targetDate;

  // 작업자(사용자 or 배치)
  @Column(length = 15, nullable = false)
  private String worker;

  // 작업일시
  @CreatedDate
  @Column(nullable = false)
  private LocalDateTime jobTime;

  // 결과
  @Column(length = 10, nullable = false)
  @Enumerated(EnumType.STRING)
  private ResultType result;

  public SyncJob(IndexInfo indexInfo, JobType jobType, LocalDate targetDate, String worker,
      ResultType result) {
    this.indexInfo = indexInfo;
    this.jobType = jobType;
    this.targetDate = targetDate;
    this.worker = worker;
    this.result = result;
  }
}
