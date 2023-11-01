package com.template.server.domain.image.repository;

import com.template.server.domain.image.entity.Gallery;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {
    Page<Gallery> findByMemberEmail(String email, Pageable pageable);

    Page<Gallery> findByPromptContaining(String prompt, Pageable pageable);

    Page<Gallery> findByTagsName(String tagName, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM Gallery g WHERE g.galleryId = :galleryId")
    Optional<Gallery> findByGalleryIdForUpdate(Long galleryId);

    @Query("SELECT g, COUNT(c) FROM Gallery g " +
           "LEFT JOIN ImageComment c ON c.gallery = g " +
           "GROUP BY g")
    Page<Object[]> findAllWithComment(Pageable pageable);
}

