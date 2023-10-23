package com.template.server.domain.image.service;

import com.template.server.domain.image.dto.GalleryDto;
import com.template.server.domain.image.repository.GalleryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GalleryService {

    private final GalleryRepository galleryRepository;

    @Transactional(readOnly = true)
    public Page<GalleryDto> galleryList(Pageable pageable){
        return galleryRepository.findAll(pageable).map(GalleryDto::from);
    }

    @Transactional(readOnly = true)
    public Page<GalleryDto> findGalleryList(String prompt, Pageable pageable){
        return galleryRepository.findByPromptContaining(prompt, pageable).map(GalleryDto::from);
    }

}
