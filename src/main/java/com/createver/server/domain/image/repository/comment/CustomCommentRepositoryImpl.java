package com.createver.server.domain.image.repository.comment;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.createver.server.domain.image.entity.ImageComment;
import com.createver.server.domain.image.entity.QImageComment;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<ImageComment> findByGalleryId(Long galleryId, Pageable pageable) {
        QImageComment imageComment = QImageComment.imageComment;

        JPAQuery<ImageComment> query = queryFactory.selectFrom(imageComment)
                .where(imageComment.gallery.galleryId.eq(galleryId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if(!ObjectUtils.isEmpty(pageable)){
            for (Sort.Order order : pageable.getSort()) {
                PathBuilder<ImageComment> pathBuilder = new PathBuilder<>(imageComment.getType(), imageComment.getMetadata());
                query.orderBy(
                        new OrderSpecifier(
                                order.getDirection().isAscending() ? Order.ASC : Order.DESC,
                                pathBuilder.get(order.getProperty())));
            }
        }

        List<ImageComment> content = query.fetch();

        JPAQuery<Long> countQuery = queryFactory.select(imageComment.count())
                .from(imageComment)
                .where(imageComment.gallery.galleryId.eq(galleryId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
