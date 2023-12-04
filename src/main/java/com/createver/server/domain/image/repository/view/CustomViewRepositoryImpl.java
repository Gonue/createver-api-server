package com.createver.server.domain.image.repository.view;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.createver.server.domain.image.entity.ImageView;
import com.createver.server.domain.image.entity.QImageView;
import com.createver.server.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomViewRepositoryImpl implements CustomViewRepository{

    private final JPAQueryFactory queryFactory;


    @Override
    public Long countByGalleryId(Long galleryId) {
        QImageView imageView = QImageView.imageView;
        return queryFactory
            .select(imageView.count())
            .from(imageView)
            .where(imageView.gallery.galleryId.eq(galleryId))
            .fetchOne();
    }

    @Override
    public List<ImageView> findByMemberOrderByViewedAtDesc(Member member) {
        QImageView imageView = QImageView.imageView;
        return queryFactory
            .selectFrom(imageView)
            .where(imageView.member.eq(member))
            .orderBy(imageView.viewedAt.desc())
            .fetch();
    }
}
