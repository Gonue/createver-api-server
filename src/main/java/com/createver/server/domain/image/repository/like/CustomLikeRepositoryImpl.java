package com.createver.server.domain.image.repository.like;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.QImageLike;
import com.createver.server.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomLikeRepositoryImpl implements CustomLikeRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByMemberAndGallery(Member member, Gallery gallery) {
        QImageLike imageLike = QImageLike.imageLike;
        return queryFactory.selectFrom(imageLike)
                .where(imageLike.member.eq(member)
                        .and(imageLike.gallery.eq(gallery)))
                .fetchFirst() != null;
    }
}
