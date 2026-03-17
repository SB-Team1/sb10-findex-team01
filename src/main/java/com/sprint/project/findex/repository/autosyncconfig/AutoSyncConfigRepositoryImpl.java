package com.sprint.project.findex.repository.autosyncconfig;

import static com.sprint.project.findex.entity.QAutoSyncConfig.autoSyncConfig;
import static com.sprint.project.findex.entity.QIndexInfo.indexInfo;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigListRequest;
import com.sprint.project.findex.entity.AutoSyncConfig;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AutoSyncConfigRepositoryImpl implements AutoSyncConfigRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<AutoSyncConfig> findListByCursor(AutoSyncConfigListRequest condition) {
    return queryFactory
        .selectFrom(autoSyncConfig)
        .join(autoSyncConfig.indexInfo, indexInfo).fetchJoin()
        .where(
            indexInfoIdEq(condition.getIndexInfoId()),
            enabledEq(condition.getEnabled()),
            cursorCondition(condition)
        )
        .orderBy(
            sortSpecifier(condition),
            autoSyncConfig.id.asc()
        )

        .limit(condition.getSize() + 1)
        .fetch();
  }

  private BooleanExpression indexInfoIdEq(Long indexInfoId) {
    return indexInfoId != null ? indexInfo.id.eq(indexInfoId) : null;
  }

  private BooleanExpression enabledEq(Boolean enabled) {
    return enabled != null ? autoSyncConfig.enabled.eq(enabled) : null;
  }


  private BooleanExpression cursorCondition(AutoSyncConfigListRequest condition) {
    if (condition.getCursor() == null || condition.getIdAfter() == null) {
      return null;
    }

    if ("indexInfo.indexName".equals(condition.getSortField())) {
      return indexInfo.indexName.gt(condition.getCursor())
          .or(indexInfo.indexName.eq(condition.getCursor()).and(autoSyncConfig.id.gt(condition.getIdAfter().intValue())));
    }
    else if ("enabled".equals(condition.getSortField())) {
      Boolean cursorBool = Boolean.valueOf(condition.getCursor());
      return autoSyncConfig.enabled.eq(cursorBool).and(autoSyncConfig.id.gt(condition.getIdAfter().intValue()));
    }

    return autoSyncConfig.id.gt(condition.getIdAfter().intValue());
  }


  private OrderSpecifier<?> sortSpecifier(AutoSyncConfigListRequest condition) {
    Order direction = "desc".equalsIgnoreCase(condition.getSortDirection()) ? Order.DESC : Order.ASC;

    if ("enabled".equals(condition.getSortField())) {
      return new OrderSpecifier<>(direction, autoSyncConfig.enabled);
    }
    return new OrderSpecifier<>(direction, indexInfo.indexName);
  }


  @Override
  public long countByCondition(AutoSyncConfigListRequest condition) {
    Long count = queryFactory
        .select(autoSyncConfig.count())
        .from(autoSyncConfig)
        .join(autoSyncConfig.indexInfo, indexInfo)
        .where(
            indexInfoIdEq(condition.getIndexInfoId()),
            enabledEq(condition.getEnabled())
        )
        .fetchOne();

    return count == null ? 0L : count;
  }
}
