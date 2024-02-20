package com.createver.server.domain.image.service.gallery;

import com.createver.server.domain.image.dto.GalleryDto;
import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.ImageTag;
import com.createver.server.domain.image.repository.gallery.GalleryRepository;
import com.createver.server.domain.member.entity.Member;
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

    @Transactional
    public Gallery createGallery(String prompt, String s3Url, int option, List<ImageTag> tags, Member member) {
        Gallery gallery = Gallery.builder()
                .prompt(prompt)
                .storageUrl(s3Url)
                .option(option)
                .tags(tags)
                .member(member)
                .build();
        return galleryRepository.save(gallery);
    }

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

    @Transactional(readOnly = true)
    public Page<GalleryDto> adminGalleryList(Pageable pageable) {
        return galleryRepository.findAllGalleriesWithoutFilter(pageable);
    }
}
