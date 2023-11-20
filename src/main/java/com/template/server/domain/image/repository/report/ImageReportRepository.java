package com.template.server.domain.image.repository;

import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.image.entity.ImageReport;
import com.template.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageReportRepository extends JpaRepository<ImageReport, Long> {
    boolean existsByMemberAndGallery(Member member, Gallery gallery);

}
