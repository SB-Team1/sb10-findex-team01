package com.sprint.project.findex.repository.indexdata.querydsl;

import static com.sprint.project.findex.entity.QIndexData.indexData;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.findex.dto.SortDirection;
import com.sprint.project.findex.dto.indexdata.CursorPageIndexDataRequest;
import com.sprint.project.findex.dto.indexdata.IndexDataCsvExportRequest;
import com.sprint.project.findex.dto.indexdata.IndexDataSortField;
import com.sprint.project.findex.entity.IndexData;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class IndexDataQDSLRepositoryImpl implements IndexDataQDSLRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Slice<IndexData> findCursorPage(CursorPageIndexDataRequest request) {
    List<IndexData> content = queryFactory
        .selectFrom(indexData)
        .where(
            eqIndexInfoId(request.indexInfoId()),
            cursorOrNull(request),
            betweenDates(request.startDate(), request.endDate())
        )
        .orderBy(
            getOrderSpecifier(request.sortField(), request.sortDirection()),
            indexData.id.desc()
        )
        .limit(request.size() + 1)
        .fetch();

    boolean hasNext = false;
    if (content.size() > request.size()) {
      content.remove(content.size() - 1);
      hasNext = true;
    }
    return new SliceImpl<>(content, PageRequest.of(0, request.size()), hasNext);
  }

  @Override
  public List<IndexData> findAllForExport(IndexDataCsvExportRequest request) {
    return queryFactory
        .selectFrom(indexData)
        .where(
            eqIndexInfoId(request.indexInfoId()),
            betweenDates(request.startDate(), request.endDate())
            )
        .orderBy(getOrderSpecifier(request.sortField(), request.sortDirection()))
        .fetch();
  }

  @Override
  public Long countByRequest(CursorPageIndexDataRequest request) {
    return queryFactory.
        select(indexData.count())
        .from(indexData)
        .where(
            eqIndexInfoId(request.indexInfoId()),
            betweenDates(request.startDate(), request.endDate())
            )
        .fetchOne();
  }

  private BooleanExpression eqIndexInfoId(Long indexInfoId) {
    return indexInfoId != null ? indexData.indexInfo.id.eq(indexInfoId) : null;
  }

  private BooleanExpression betweenDates(LocalDate startTime, LocalDate endDate) {
    if (startTime != null && endDate != null) {
      return indexData.baseDate.between(startTime, endDate);
    }
    if (startTime != null) {
      return indexData.baseDate.goe(startTime);
    }
    if (endDate != null) {
      return indexData.baseDate.loe(endDate);
    }
    return null;
  }

  private BooleanExpression cursorOrNull(CursorPageIndexDataRequest request) {
    if (request.cursor() == null || request.idAfter() == null) {
      return null;
    }

    IndexDataSortField sortField = request.sortField();
    String sortDirection = request.sortDirection().name();
    Long idAfter = request.idAfter();

    Comparable<?> parsedCursor = sortField.parseCursor(request.cursor());
    PathBuilder<IndexData> pathBuilder = new PathBuilder<>(IndexData.class, "indexData");
    ComparablePath<Comparable> path = pathBuilder.getComparable(sortField.getName(),
        (Class) sortField.getType());

    return compareCursor(path, parsedCursor, sortDirection, idAfter);
  }

  private <T extends Comparable<?>> BooleanExpression compareCursor(ComparablePath<T> path,
      T cursorValue, String sortDirection, Long idAfter) {
    boolean isAsc = "asc".equalsIgnoreCase(sortDirection);

    if (isAsc) {
      return path.gt(cursorValue)
          .or(path.eq(cursorValue)
              .and(indexData.id.lt(idAfter)));
    } else {
      return path.lt(cursorValue)
          .or(path.eq(cursorValue)
              .and(indexData.id.lt(idAfter)));
    }
  }

  private OrderSpecifier<?> getOrderSpecifier(IndexDataSortField sortField,
      SortDirection sortDirection) {
    Order order = (sortDirection == SortDirection.ASC) ? Order.ASC : Order.DESC;
    PathBuilder<IndexData> pathBuilder = new PathBuilder<>(IndexData.class, "indexData");

    return new OrderSpecifier(order,
        pathBuilder.getComparable(sortField.getName(), sortField.getType()));
  }

}
