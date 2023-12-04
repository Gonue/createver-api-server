package com.createver.server.domain.music.repository.album;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomAlbumRepositoryImpl implements CustomAlbumRepository{

    private final JPAQueryFactory queryFactory;
}
