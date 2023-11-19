package com.template.server.domain.image.repository;

import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.image.entity.ImageLike;
import com.template.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageLikeRepository extends JpaRepository<ImageLike, Long> {
    boolean existsByMemberAndGallery(Member member, Gallery gallery);
}
