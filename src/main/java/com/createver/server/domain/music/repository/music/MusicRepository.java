package com.createver.server.domain.music.repository.music;

import com.createver.server.domain.music.entity.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {
    Optional<Music> findByPredictionId(String predictionId);
    Page<Music> findByMemberEmail(String email, Pageable pageable);
}
