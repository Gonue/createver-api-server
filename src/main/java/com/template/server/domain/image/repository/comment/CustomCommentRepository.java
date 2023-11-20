package com.template.server.domain.image.repository.comment;

import com.template.server.domain.image.entity.ImageComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomCommentRepository {
    Page<ImageComment> findByGalleryId(Long galleryId, Pageable pageable);
}
