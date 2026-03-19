package com.sprint.project.findex.entity;

import com.sprint.project.findex.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 위해 추가 (외부 노출 방지)
@Builder
@Getter
@Entity
@Table(name = "auto_sync_configs")
public class AutoSyncConfig extends BaseEntity {

  // 지수 정보
  @OneToOne
  @JoinColumn(name = "index_info_id", nullable = false, unique = true)
  private IndexInfo indexInfo;

  // 생성자
  // 자동 연동 활성화
  @Column(name = "enabled", nullable = false)
  private boolean enabled = false;

  public AutoSyncConfig(IndexInfo indexInfo){
    this.indexInfo = indexInfo;
  }

  public void updateEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
