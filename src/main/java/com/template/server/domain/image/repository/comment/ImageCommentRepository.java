package com.template.server.domain.image.repository;

import com.template.server.domain.image.entity.ImageComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageCommentRepository extends JpaRepository<ImageComment, Long> {
    Page<ImageComment> findByGallery_GalleryId(Long galleryId, Pageable pageable);
}
