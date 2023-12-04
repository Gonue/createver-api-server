package com.createver.server.domain.image.repository.comment;

import com.createver.server.domain.image.entity.ImageComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageCommentRepository extends JpaRepository<ImageComment, Long>, CustomCommentRepository {
}
