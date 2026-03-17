package com.sprint.project.findex.repository.querydsl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.findex.dto.SortDirection;
import com.sprint.project.findex.dto.indexinfo.IndexInfoCursorPageRequest;
import com.sprint.project.findex.dto.indexinfo.IndexInfoSortField;
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
  public List<IndexInfo> findByCursor(IndexInfoCursorPageRequest request) {
    return queryFactory.selectFrom(qIndexInfo)
        .where(
            predicateOrNull(qIndexInfo.id::gt, request.idAfter()),
            predicateOrNull(qIndexInfo.indexClassification::eq, request.indexClassification()),
            predicateOrNull(qIndexInfo.indexName::eq, request.indexName()),
            predicateOrNull(qIndexInfo.favorite::eq, request.favorite()),
            cursorOrNull(request)
        ).limit(request.size() + 1)
        .orderBy(getOrderSpecifier(request.sortField(), request.sortDirection()))
        .fetch();
  }

  private OrderSpecifier<?> getOrderSpecifier(IndexInfoSortField sortField,
      SortDirection sortDirection) {
    Order order = (sortDirection == SortDirection.ASC) ? Order.ASC : Order.DESC;
    String fieldName = sortField.getName();
    PathBuilder<IndexInfo> pathBuilder = new PathBuilder<>(IndexInfo.class, "indexInfo");
    return new OrderSpecifier<>(order, pathBuilder.getComparable(fieldName, Comparable.class));
  }

  private Predicate cursorOrNull(IndexInfoCursorPageRequest request) {
    if (request.indexClassification() == null) {
      return predicateOrNull(qIndexInfo.indexClassification::eq, request.cursor());
    }
    return null;
  }

  private <T> Predicate predicateOrNull(Function<T, Predicate> action, T value) {
    return Optional.ofNullable(value).map(action).orElse(null);
  }

}
