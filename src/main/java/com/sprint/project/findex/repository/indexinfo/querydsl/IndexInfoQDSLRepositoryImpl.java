package com.sprint.project.findex.repository.indexinfo.querydsl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.findex.dto.indexinfo.IndexInfoCursorPageRequest;
import com.sprint.project.findex.dto.indexinfo.IndexInfoSummaryDto;
import com.sprint.project.findex.entity.IndexInfo;
import com.sprint.project.findex.entity.QIndexInfo;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class IndexInfoQDSLRepositoryImpl implements IndexInfoQDSLRepository {

  private final JPAQueryFactory queryFactory;
  private final QIndexInfo qIndexInfo = QIndexInfo.indexInfo;

  @Override
  public List<IndexInfoSummaryDto> findDistinctClassificationsAndNames() {
    return queryFactory.select(
            Projections.constructor(IndexInfoSummaryDto.class, qIndexInfo.id.min(),
                qIndexInfo.indexClassification, qIndexInfo.indexName))
        .from(qIndexInfo)
        .groupBy(qIndexInfo.indexClassification, qIndexInfo.indexName)
        .fetch();
  }

  @Override
  public List<IndexInfo> findByCursor(IndexInfoCursorPageRequest request) {
    return queryFactory.selectFrom(qIndexInfo)
        .where(
            condition(request),
            predicateOrNull(qIndexInfo.favorite::eq, request.getFavorite())
        ).limit(request.getSize() + 1)
        .orderBy(getOrderSpecifier(request), getIdOrderSpecifier(request))
        .fetch();
  }

  @Override
  public Long getTotalElements(IndexInfoCursorPageRequest request) {
    return queryFactory.select(qIndexInfo.id.count())
        .from(qIndexInfo)
        .where(
            filter(request),
            predicateOrNull(qIndexInfo.favorite::eq, request.getFavorite())
        )
        .fetchOne();
  }

  private BooleanExpression filter(IndexInfoCursorPageRequest request) {
    if (request.getIndexClassification() != null) {
      return qIndexInfo.indexClassification.containsIgnoreCase(request.getIndexClassification());
    }
    if (request.getIndexName() != null) {
      return qIndexInfo.indexName.containsIgnoreCase(request.getIndexName());
    }
    return null;
  }

  private BooleanExpression condition(IndexInfoCursorPageRequest request) {
    BooleanExpression searchFilter = filter(request);
    if (searchFilter == null) {
      return conditionByCursor(request);
    }
    return searchFilter;
  }

  private BooleanExpression conditionByCursor(IndexInfoCursorPageRequest request) {
    String cursor = request.getCursor();
    Long idAfter = request.getIdAfter();
    if (cursor == null || idAfter == null) {
      return null;
    }
    PathBuilder<IndexInfo> pathBuilder = new PathBuilder<>(IndexInfo.class, "indexInfo");
    ComparablePath<String> path = pathBuilder.getComparable(request.getSortField().getName(),
        String.class);
    if (request.getSortDirection() == Order.DESC) {
      return path.lt(cursor)
          .or(path.eq(cursor)
              .and(qIndexInfo.id.loe(idAfter)));
    }
    return path.gt(cursor)
        .or(path.eq(cursor)
            .and(qIndexInfo.id.goe(idAfter)));
  }

  private OrderSpecifier<?> getOrderSpecifier(IndexInfoCursorPageRequest request) {
    String fieldName = request.getSortField().getName();
    PathBuilder<IndexInfo> pathBuilder = new PathBuilder<>(IndexInfo.class, "indexInfo");
    return new OrderSpecifier<>(request.getSortDirection(),
        pathBuilder.getComparable(fieldName, Comparable.class));
  }

  private OrderSpecifier<?> getIdOrderSpecifier(IndexInfoCursorPageRequest request) {
    if (request.getSortDirection() == Order.DESC) {
      return qIndexInfo.id.desc();
    }
    return qIndexInfo.id.asc();
  }

  private <T> BooleanExpression predicateOrNull(Function<T, BooleanExpression> action, T value) {
    return Optional.ofNullable(value).map(action).orElse(null);
  }

}
