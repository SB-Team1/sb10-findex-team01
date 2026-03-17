package com.sprint.project.findex.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Entity
@Table(name = "auto_sync_configs")
public class AutoSyncConfig {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  // 지수 정보
  @OneToOne
  @JoinColumn(name = "index_info_id", nullable = false, unique = true)
  private IndexInfo indexInfo;

  // 생성자
  public AutoSyncConfig(Integer Id, IndexInfo indexInfo){
    this.id = Id;
    this.indexInfo = indexInfo;
    this.enabled = false;

    this.createdAt = Instant.now();
    this.updatedAt = createdAt;
  }

  // 자동 연동 활성화
  @Column(name = "enabled", nullable = false)
  private boolean enabled;

  public void updateEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
