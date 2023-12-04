package com.createver.server.domain.image.repository.view;

import com.createver.server.domain.image.entity.ImageView;
import com.createver.server.domain.member.entity.Member;

import java.util.List;

public interface CustomViewRepository {
    Long countByGalleryId(Long galleryId);
    List<ImageView> findByMemberOrderByViewedAtDesc(Member member);
}
