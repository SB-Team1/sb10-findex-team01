package com.sprint.project.findex.repository;

import com.sprint.project.findex.dto.dashboard.DashboardQueryDto;
import com.sprint.project.findex.entity.IndexData;
import com.sprint.project.findex.entity.DeletedStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DashboardRepository extends JpaRepository<IndexData, Long> {

  // 쿼리문 안에서 IndexPerformanceDto를 바로 매핑하려니 계산하는 과정에서 타입 추론 오류발생
  // DashboardQueryDto로 우회해서 서비스에서 계산후 처리하는 방향으로 결정
  // 기간 내 변화가 있었던 즐겨찾기 지수 조회
  // current = 최신 데이터
  // before = period 시작일 이후 가장 이른 데이터
  @Query("""
      select new com.sprint.project.findex.dto.dashboard.DashboardQueryDto(
          i.id,
          i.indexClassification,
          i.indexName,
          current.closingPrice,
          before.closingPrice
      )
      from IndexInfo i
      join IndexData current
        on current.indexInfo.id = i.id
       and current.isDeleted = :deletedStatus
       and current.baseDate = (
           select max(c.baseDate)
           from IndexData c
           where c.indexInfo.id = i.id
             and c.isDeleted = :deletedStatus
       )
      join IndexData before
        on before.indexInfo.id = i.id
       and before.isDeleted = :deletedStatus
       and before.baseDate = (
           select min(b.baseDate)
           from IndexData b
           where b.indexInfo.id = i.id
             and b.baseDate >= :targetDate
             and b.isDeleted = :deletedStatus
       )
      where i.favorite = true
        and i.isDeleted = :deletedStatus
        and current.closingPrice <> before.closingPrice
      """)
  List<DashboardQueryDto> findChangedFavoriteIndexPerformance(
      @Param("targetDate") LocalDate targetDate,
      @Param("deletedStatus") DeletedStatus deletedStatus
  );

  // 기간 내 전체 지수의 랭킹 조회
  // current = 최신 데이터
  // before = period 시작일 이후 가장 이른 데이터
  @Query("""
      select new com.sprint.project.findex.dto.dashboard.DashboardQueryDto(
          i.id,
          i.indexClassification,
          i.indexName,
          current.closingPrice,
          before.closingPrice
      )
      from IndexInfo i
      join IndexData current
        on current.indexInfo.id = i.id
       and current.isDeleted = :deletedStatus
       and current.baseDate = (
           select max(c.baseDate)
           from IndexData c
           where c.indexInfo.id = i.id
             and c.baseDate <= :today
             and c.isDeleted = :deletedStatus
       )
      join IndexData before
        on before.indexInfo.id = i.id
       and before.isDeleted = :deletedStatus
       and before.baseDate = (
           select max(b.baseDate)
           from IndexData b
           where b.indexInfo.id = i.id
             and b.baseDate <= :compareDate
             and b.isDeleted = :deletedStatus
       )
      where i.isDeleted = :deletedStatus
  """)
  List<DashboardQueryDto> findAllIndexRanking(
      @Param("today") LocalDate today,
      @Param("compareDate") LocalDate compareDate,
      @Param("deletedStatus") DeletedStatus deletedStatus
  );

  // 기간 내 특정 지수의 랭킹 조회
  // current = 최신 데이터
  // before = period 시작일 이후 가장 이른 데이터
  @Query("""
      select new com.sprint.project.findex.dto.dashboard.DashboardQueryDto(
          i.id,
          i.indexClassification,
          i.indexName,
          current.closingPrice,
          before.closingPrice
      )
      from IndexInfo i
      join IndexData current
        on current.indexInfo.id = i.id
       and current.isDeleted = :deletedStatus
       and current.baseDate = (
           select max(c.baseDate)
           from IndexData c
           where c.indexInfo.id = i.id
             and c.baseDate <= :today
             and c.isDeleted = :deletedStatus
       )
      join IndexData before
        on before.indexInfo.id = i.id
       and before.isDeleted = :deletedStatus
       and before.baseDate = (
           select max(b.baseDate)
           from IndexData b
           where b.indexInfo.id = i.id
             and b.baseDate <= :compareDate
             and b.isDeleted = :deletedStatus
       )
      where i.id = :indexInfoId
        and i.isDeleted = :deletedStatus
  """)
  List<DashboardQueryDto> findIndexRankingByIndexInfoId(
      @Param("indexInfoId") Long indexInfoId,
      @Param("today") LocalDate today,
      @Param("compareDate") LocalDate compareDate,
      @Param("deletedStatus") DeletedStatus deletedStatus
  );
}
