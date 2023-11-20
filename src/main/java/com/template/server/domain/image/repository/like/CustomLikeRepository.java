package com.template.server.domain.image.repository.like;

import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.member.entity.Member;

public interface CustomLikeRepository {
    boolean existsByMemberAndGallery(Member member, Gallery gallery);
}
