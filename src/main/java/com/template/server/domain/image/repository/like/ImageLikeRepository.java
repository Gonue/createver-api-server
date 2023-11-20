package com.template.server.domain.image.repository.like;

import com.template.server.domain.image.entity.ImageLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageLikeRepository extends JpaRepository<ImageLike, Long>, CustomLikeRepository {
}
