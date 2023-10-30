package com.template.server.domain.image.service;

import com.template.server.domain.image.dto.GalleryDto;
import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.image.repository.GalleryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public Page<GalleryDto> getGalleriesByTagName(String tagName, Pageable pageable){
        return galleryRepository.findByTagsName(tagName, pageable).map(GalleryDto::from);
    }

    @Transactional(readOnly = true)
    public Page<GalleryDto> galleryListWithCommentAndLikeCount(Pageable pageable){
        Page<Object[]> results = galleryRepository.findAllWithCommentAndLikeCount(pageable);
        List<GalleryDto> dtos = results.getContent().stream()
            .map(result -> {
                Gallery gallery = (Gallery) result[0];
                Long commentCount = (Long) result[1];
                Long likeCount = (Long) result[2];
                return GalleryDto.from(gallery, commentCount, likeCount);
            })
            .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, results.getTotalElements());
    }
}
