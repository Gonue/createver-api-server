package com.createver.server.domain.image.repository.tag;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.createver.server.domain.image.entity.ImageTag;
import com.createver.server.domain.image.entity.QImageTag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CustomTagRepositoryImpl implements CustomTagRepository{

    private final JPAQueryFactory queryFactory;


    @Override
    public Optional<ImageTag> findByName(String name) {
        QImageTag imageTag = QImageTag.imageTag;
        return Optional.ofNullable(queryFactory
            .selectFrom(imageTag)
            .where(imageTag.name.eq(name))
            .fetchFirst());
    }

    @Override
    public List<ImageTag> findByNameIn(Set<String> names) {
        QImageTag imageTag = QImageTag.imageTag;
        return queryFactory
            .selectFrom(imageTag)
            .where(imageTag.name.in(names))
            .fetch();
    }
}
