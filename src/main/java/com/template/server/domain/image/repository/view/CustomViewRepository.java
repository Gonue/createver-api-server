package com.template.server.domain.image.repository.view;

import com.template.server.domain.image.entity.ImageView;
import com.template.server.domain.member.entity.Member;

import java.util.List;

public interface CustomViewRepository {
    Long countByGalleryId(Long galleryId);
    List<ImageView> findByMemberOrderByViewedAtDesc(Member member);
}
