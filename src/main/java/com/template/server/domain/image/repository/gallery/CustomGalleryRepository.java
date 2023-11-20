package com.template.server.domain.image.repository.gallery;

import com.template.server.domain.image.dto.GalleryDto;
import com.template.server.domain.image.entity.Gallery;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface CustomGalleryRepository {
    Page<Gallery> findGalleryByMemberEmail(String email, Pageable pageable);
    Page<Gallery> findGalleryByTagsName(String tagName, Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Gallery> findGalleryByIdForUpdate(Long galleryId);
    Page<GalleryDto> findAllGalleriesWithComment(Pageable pageable);
    Page<Gallery> hasOptionsAndPrompt(List<Integer> options, String prompt, Pageable pageable);
}