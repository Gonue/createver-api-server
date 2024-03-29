package com.createver.server.domain.music.repository.album;

import com.createver.server.domain.music.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long>, CustomAlbumRepository {
    Page<Album> findByMemberEmail(String email, Pageable pageable);
}
