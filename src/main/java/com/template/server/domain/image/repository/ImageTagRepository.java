package com.template.server.domain.image.repository;

import com.template.server.domain.image.entity.ImageTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageTagRepository extends JpaRepository<ImageTag, Long> {
    Optional<ImageTag> findByName(String name);
}
