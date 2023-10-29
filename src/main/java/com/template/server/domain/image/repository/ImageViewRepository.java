package com.template.server.domain.image.repository;

import com.template.server.domain.image.entity.ImageView;
import com.template.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageViewRepository extends JpaRepository<ImageView, Long> {
    Long countByGalleryGalleryId(Long galleryId);
    List<ImageView> findByMemberOrderByViewedAtDesc(Member member);
}
