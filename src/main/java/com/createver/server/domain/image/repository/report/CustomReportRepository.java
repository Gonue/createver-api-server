package com.createver.server.domain.image.repository.report;

import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.member.entity.Member;

public interface CustomReportRepository {
    boolean existsByMemberAndGallery(Member member, Gallery gallery);
}
