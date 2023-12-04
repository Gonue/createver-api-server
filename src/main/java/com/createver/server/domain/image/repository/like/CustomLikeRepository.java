package com.createver.server.domain.image.repository.like;

import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.member.entity.Member;

public interface CustomLikeRepository {
    boolean existsByMemberAndGallery(Member member, Gallery gallery);
}
