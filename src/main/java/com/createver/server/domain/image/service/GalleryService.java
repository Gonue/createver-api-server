package com.createver.server.domain.image.service;

import com.createver.server.domain.image.dto.GalleryDto;
import com.createver.server.domain.image.repository.gallery.GalleryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GalleryService {

    private final GalleryRepository galleryRepository;

    @Transactional(readOnly = true)
    public Page<GalleryDto> findGalleryListByOptionsAndPrompt(List<Integer> options, String prompt, Pageable pageable) {
        return galleryRepository.hasOptionsAndPrompt(options, prompt, pageable).map(GalleryDto::from);
    }

    @Transactional(readOnly = true)
    public Page<GalleryDto> getGalleriesByTagName(String tagName, Pageable pageable){
        return galleryRepository.findGalleryByTagsName(tagName, pageable).map(GalleryDto::from);
    }

    @Transactional(readOnly = true)
    public Page<GalleryDto> galleryListWithComment(Pageable pageable){
        return galleryRepository.findAllGalleriesWithComment(pageable);
    }
}
