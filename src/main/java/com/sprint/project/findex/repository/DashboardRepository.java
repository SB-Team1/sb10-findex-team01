package com.sprint.project.findex.repository;

import com.sprint.project.findex.dto.dashboard.DashboardQueryDto;
import com.sprint.project.findex.entity.IndexData;
import com.sprint.project.findex.repository.projection.DashboardRankingProjection;
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
      @Param("targetDate") LocalDate targetDate
  );

  @Query(value = """
    -- current_data: today 이하 데이터 중에서 지수별(index_info_id) 가장 최근 종가 1건을 가져옴
    WITH current_data AS (
        SELECT DISTINCT ON (d.index_info_id)
               d.index_info_id,
               d.closing_price,
               d.base_date
        FROM index_datas d
        WHERE d.is_deleted = :deletedStatus
          AND d.base_date <= :today
        ORDER BY d.index_info_id, d.base_date DESC
    ),
    -- before_data: compareDate 이하 데이터 중에서 지수별 가장 최근 종가 1건을 가져옴
    before_data AS (
        SELECT DISTINCT ON (d.index_info_id)
               d.index_info_id,
               d.closing_price,
               d.base_date
        FROM index_datas d
        WHERE d.is_deleted = :deletedStatus
          AND d.base_date <= :compareDate
        ORDER BY d.index_info_id, d.base_date DESC
    )
    -- index_infos와 current/before 결과를 조인해서
    -- 랭킹 계산에 필요한 현재 종가 / 비교 종가를 한 번에 가져옴
    SELECT
        i.id AS id,
        i.index_classification AS indexClassification,
        i.index_name AS indexName,
        c.closing_price AS currentClosingPrice,
        b.closing_price AS beforeClosingPrice
    FROM index_infos i
    JOIN current_data c
      ON c.index_info_id = i.id
    JOIN before_data b
      ON b.index_info_id = i.id
    WHERE i.is_deleted = :deletedStatus
    """, nativeQuery = true)
  List<DashboardRankingProjection> findAllIndexRanking(
      @Param("today") LocalDate today,
      @Param("compareDate") LocalDate compareDate
  );

  // 특정 지수 1건에 대해서만
  // today 이하 최신 종가 1건, compareDate 이하 최신 종가 1건을 LATERAL로 각각 조회
  @Query(value = """
    SELECT
        i.id AS id,
        i.index_classification AS indexClassification,
        i.index_name AS indexName,
        current_data.closing_price AS currentClosingPrice,
        before_data.closing_price AS beforeClosingPrice
    FROM index_infos i
    JOIN LATERAL (
        SELECT d.closing_price, d.base_date
        FROM index_datas d
        WHERE d.index_info_id = i.id
          AND d.is_deleted = :deletedStatus
          AND d.base_date <= :today
        ORDER BY d.base_date DESC
        LIMIT 1
    ) current_data ON true
    JOIN LATERAL (
        SELECT d.closing_price, d.base_date
        FROM index_datas d
        WHERE d.index_info_id = i.id
          AND d.is_deleted = :deletedStatus
          AND d.base_date <= :compareDate
        ORDER BY d.base_date DESC
        LIMIT 1
    ) before_data ON true
    WHERE i.id = :indexInfoId
      AND i.is_deleted = :deletedStatus
    """, nativeQuery = true)
  List<DashboardRankingProjection> findIndexRankingByIndexInfoId(
      @Param("indexInfoId") Long indexInfoId,
      @Param("today") LocalDate today,
      @Param("compareDate") LocalDate compareDate
  );

  // 윈도우 함수 사용으로 인한 native 쿼리 작성
  @Query(value = """
    WITH latest_date AS (
        SELECT MAX(base_date) AS end_date
        FROM index_datas
        WHERE index_info_id = :indexInfoId
          AND is_deleted = :isDeleted
    ),
    range_start AS (
        SELECT
            CASE
                WHEN :periodType = 'MONTHLY'
                    THEN end_date - INTERVAL '1 month'
                WHEN :periodType = 'QUARTERLY'
                    THEN end_date - INTERVAL '3 months'
                WHEN :periodType = 'YEARLY'
                    THEN end_date - INTERVAL '1 year'
            END AS start_date,
            end_date
        FROM latest_date
    ),
    raw_data AS (
        SELECT
            id.base_date,
            id.closing_price
        FROM index_datas id
        JOIN range_start r ON TRUE
        WHERE id.index_info_id = :indexInfoId
          AND id.is_deleted = :isDeleted
          -- 이동평균 계산을 위해 앞쪽 데이터를 추가 확보
          AND id.base_date >= r.start_date - INTERVAL '20 days'
          AND id.base_date <= r.end_date
    ),
    ma_calculated AS (
        SELECT
            base_date,
            closing_price,
            -- 여기가 윈도우 함수 (실제 이동평균 계산)
            AVG(closing_price) OVER (
                ORDER BY base_date
                ROWS BETWEEN 4 PRECEDING AND CURRENT ROW
            ) AS ma5,
            AVG(closing_price) OVER (
                ORDER BY base_date
                ROWS BETWEEN 19 PRECEDING AND CURRENT ROW
            ) AS ma20
        FROM raw_data
    )
    SELECT
        m.base_date,
        m.closing_price,
        m.ma5,
        m.ma20
    FROM ma_calculated m
    JOIN range_start r ON TRUE
    WHERE m.base_date >= r.start_date
    ORDER BY m.base_date
    """, nativeQuery = true)
  // dto로 바로 매핑이 어렵기 때문에 한 행(row)을 Object 배열로 받음
  List<Object[]> findIndexChartData(
      @Param("indexInfoId") Long indexInfoId,
      @Param("periodType") String periodType
  );
}
