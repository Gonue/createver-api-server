package com.template.server.domain.image.service;

import com.template.server.domain.image.dto.GalleryDto;
import com.template.server.domain.image.dto.response.GalleryRecommendationResponse;
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
    public Page<GalleryDto> galleryListWithComment(Pageable pageable){
        Page<Object[]> results = galleryRepository.findAllWithComment(pageable);
        List<GalleryDto> dtos = results.getContent().stream()
            .map(result -> {
                Gallery gallery = (Gallery) result[0];
                Long commentCount = (Long) result[1];
                return GalleryDto.from(gallery, commentCount);
            })
            .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, results.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<GalleryRecommendationResponse> galleryRecommendationList(Pageable pageable){
        Page<Object[]> results = galleryRepository.findAllWithComment(pageable);

        List<GalleryRecommendationResponse> recommendationResponses = results.getContent().stream()
                .map(result -> {
                    Gallery gallery = (Gallery) result[0];
                    Long commentCount = (Long) result[1];
                    return GalleryRecommendationResponse.from(GalleryDto.from(gallery, commentCount));
                })
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .collect(Collectors.toList());

        return new PageImpl<>(recommendationResponses, pageable, results.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<GalleryDto> galleryListSortedByLikeCountAndComments(Pageable pageable){
        Page<Object[]> results = galleryRepository.findAllWithCommentSortedByLikeCount(pageable);

        List<GalleryDto> recommendationResponses = results.getContent().stream()
            .map(result -> {
                Gallery gallery = (Gallery) result[0];
                Long commentCount = (Long) result[1];
                return GalleryDto.from(gallery, commentCount);
            })
            .collect(Collectors.toList());

        return new PageImpl<>(recommendationResponses, pageable, results.getTotalElements());
    }


}
