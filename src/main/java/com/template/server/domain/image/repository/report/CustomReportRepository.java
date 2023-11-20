package com.template.server.domain.image.repository.report;

import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.member.entity.Member;

public interface CustomReportRepository {
    boolean existsByMemberAndGallery(Member member, Gallery gallery);
}
