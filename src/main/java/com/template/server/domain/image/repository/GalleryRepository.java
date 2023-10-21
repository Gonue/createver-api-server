package com.template.server.domain.image.repository;

import com.template.server.domain.image.entity.Gallery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {
    Page<Gallery> findByMemberEmail(String email, Pageable pageable);
    Page<Gallery> findByPromptContaining(String prompt, Pageable pageable);
}

