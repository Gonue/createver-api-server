package com.template.server.domain.music.repository.album;

import com.template.server.domain.music.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long>, CustomAlbumRepository {
}
