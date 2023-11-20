package com.template.server.domain.image.repository.report;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.image.entity.QImageReport;
import com.template.server.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomReportRepositoryImpl implements CustomReportRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByMemberAndGallery(Member member, Gallery gallery) {
        QImageReport imageReport = QImageReport.imageReport;
        return queryFactory.selectFrom(imageReport)
                .where(imageReport.member.eq(member)
                        .and(imageReport.gallery.eq(gallery)))
                .fetchFirst() != null;
    }
}
