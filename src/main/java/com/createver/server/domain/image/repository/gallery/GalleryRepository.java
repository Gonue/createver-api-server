package com.createver.server.domain.image.repository.gallery;

import com.createver.server.domain.image.entity.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long>, CustomGalleryRepository {
}

