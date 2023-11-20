package com.template.server.domain.image.repository.tag;

import com.template.server.domain.image.entity.ImageTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageTagRepository extends JpaRepository<ImageTag, Long>, CustomTagRepository {
}
