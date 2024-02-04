package com.createver.server.domain.image.repository.avatar;

import com.createver.server.domain.image.entity.ImageAvatar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageAvatarRepository extends JpaRepository<ImageAvatar, Long> {
    ImageAvatar findByPredictionId(String predictionId);
    Page<ImageAvatar> findByMemberEmail(String email, Pageable pageable);
}
