package com.createver.server.domain.image.repository.gallery;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.createver.server.domain.image.dto.GalleryDto;
import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.QGallery;
import com.createver.server.domain.image.entity.QImageComment;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomGalleryRepositoryImpl implements CustomGalleryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Gallery> findGalleryByMemberEmail(String email, Pageable pageable) {
        QGallery gallery = QGallery.gallery;

        JPAQuery<Gallery> query = queryFactory.selectFrom(gallery)
                .where(gallery.member.email.eq(email))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (!ObjectUtils.isEmpty(pageable)) {
            for (Sort.Order order : pageable.getSort()) {
                PathBuilder<Gallery> pathBuilder = new PathBuilder<>(gallery.getType(), gallery.getMetadata());
                query.orderBy(
                        new OrderSpecifier(
                                order.getDirection().isAscending() ? Order.ASC : Order.DESC,
                                pathBuilder.get(order.getProperty())));
            }
        }
        List<Gallery> content = query.fetch();

        JPAQuery<Long> countQuery = queryFactory.select(gallery.count())
                .from(gallery)
                .where(gallery.member.email.eq(email));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Gallery> findGalleryByTagsName(String tagName, Pageable pageable) {
        QGallery gallery = QGallery.gallery;

        List<Gallery> galleries = queryFactory.selectFrom(gallery)
                .where(gallery.tags.any().name.eq(tagName))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(gallery.count())
                .from(gallery)
                .where(gallery.tags.any().name.eq(tagName));

        return PageableExecutionUtils.getPage(galleries, pageable, countQuery::fetchOne);
    }

    @Override
    public Optional<Gallery> findGalleryByIdForUpdate(Long galleryId) {
        QGallery gallery = QGallery.gallery;

        Gallery foundGallery = queryFactory.selectFrom(gallery)
                .where(gallery.galleryId.eq(galleryId))
                .fetchOne();

        return Optional.ofNullable(foundGallery);
    }

    @Override
    public Page<GalleryDto> findAllGalleriesWithComment(Pageable pageable) {
        QGallery gallery = QGallery.gallery;
        QImageComment imageComment = QImageComment.imageComment;

        JPAQuery<GalleryDto> query = queryFactory
                .select(Projections.constructor(GalleryDto.class,
                        gallery.galleryId,
                        gallery.prompt,
                        gallery.storageUrl,
                        gallery.option,
                        gallery.createdAt,
                        imageComment.commentId.count(),
                        gallery.likeCount,
                        gallery.downloadCount,
                        gallery.reportCount,
                        gallery.isBlinded))
                .from(gallery)
                .leftJoin(imageComment).on(imageComment.gallery.eq(gallery))
                .where(gallery.isBlinded.isFalse())
                .groupBy(gallery)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 정렬 조건
        if (!ObjectUtils.isEmpty(pageable)) {
            for (Sort.Order order : pageable.getSort()) {
                PathBuilder<Gallery> pathBuilder = new PathBuilder<>(gallery.getType(), gallery.getMetadata());
                query.orderBy(
                        new OrderSpecifier(
                                order.getDirection().isAscending() ? Order.ASC : Order.DESC,
                                pathBuilder.get(order.getProperty())));
            }
        }

        List<GalleryDto> content = query.fetch();
        JPAQuery<Long> countQuery = queryFactory
                .select(gallery.count())
                .from(gallery)
                .leftJoin(imageComment).on(imageComment.gallery.eq(gallery))
                .where(gallery.isBlinded.isFalse());


        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<GalleryDto> findAllGalleriesWithoutFilter(Pageable pageable) {
        QGallery gallery = QGallery.gallery;
        QImageComment imageComment = QImageComment.imageComment;

        JPAQuery<GalleryDto> query = queryFactory
                .select(Projections.constructor(GalleryDto.class,
                        gallery.galleryId,
                        gallery.prompt,
                        gallery.storageUrl,
                        gallery.option,
                        gallery.createdAt,
                        imageComment.commentId.count(),
                        gallery.likeCount,
                        gallery.downloadCount,
                        gallery.reportCount,
                        gallery.isBlinded))
                .from(gallery)
                .leftJoin(imageComment).on(imageComment.gallery.eq(gallery))
                .groupBy(gallery)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 정렬 조건
        if (!ObjectUtils.isEmpty(pageable)) {
            for (Sort.Order order : pageable.getSort()) {
                PathBuilder<Gallery> pathBuilder = new PathBuilder<>(gallery.getType(), gallery.getMetadata());
                query.orderBy(
                        new OrderSpecifier(
                                order.getDirection().isAscending() ? Order.ASC : Order.DESC,
                                pathBuilder.get(order.getProperty())));
            }
        }

        List<GalleryDto> content = query.fetch();
        JPAQuery<Long> countQuery = queryFactory
                .select(gallery.count())
                .from(gallery)
                .leftJoin(imageComment).on(imageComment.gallery.eq(gallery));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Gallery> hasOptionsAndPrompt(List<Integer> options, String prompt, Pageable pageable) {
        QGallery gallery = QGallery.gallery;
        BooleanBuilder builder = new BooleanBuilder();

        if (options != null && !options.isEmpty()) {
            builder.and(gallery.option.in(options));
        }

        if (prompt != null && !prompt.trim().isEmpty()) {
            builder.and(gallery.prompt.contains(prompt));
        }

        List<Gallery> galleries = queryFactory.selectFrom(gallery)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(gallery.count())
                .from(gallery)
                .where(builder);

        return PageableExecutionUtils.getPage(galleries, pageable, countQuery::fetchOne);
    }


}
