package com.template.server.domain.image.service;

import com.template.server.domain.image.dto.GalleryDto;
import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.image.repository.GalleryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GalleryServiceTest {

    @InjectMocks
    private GalleryService galleryService;

    @Mock
    private GalleryRepository galleryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGalleryList() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        List<Gallery> galleryEntityList = Arrays.asList(
            Gallery.create("prompt1", "url1", 1),
            Gallery.create("prompt2", "url2", 2)
        );
        Page<Gallery> galleryEntityPage = new PageImpl<>(galleryEntityList, pageable, galleryEntityList.size());

        // When
        when(galleryRepository.findAll(pageable)).thenReturn(galleryEntityPage);

        // Then
        Page<GalleryDto> result = galleryService.galleryList(pageable);
        assertEquals(galleryEntityList.size(), result.getContent().size());

        for (int i = 0; i < galleryEntityList.size(); i++) {
            Gallery entity = galleryEntityList.get(i);
            GalleryDto dto = result.getContent().get(i);

            assertEquals(entity.getPrompt(), dto.getPrompt());
            assertEquals(entity.getStorageUrl(), dto.getStorageUrl());
            assertEquals(entity.getOption(), dto.getOption());
        }
    }

    @Test
    void testFindGalleryList() {
        // Given
        String prompt = "prompt";
        Pageable pageable = PageRequest.of(0, 10);
        List<Gallery> galleryEntityList = Arrays.asList(
            Gallery.create("prompt1", "url1", 1),
            Gallery.create("prompt2", "url2", 2)
        );
        Page<Gallery> galleryEntityPage = new PageImpl<>(galleryEntityList, pageable, galleryEntityList.size());

        // When
        when(galleryRepository.findByPromptContaining(prompt, pageable)).thenReturn(galleryEntityPage);

        // Then
        Page<GalleryDto> result = galleryService.findGalleryList(prompt, pageable);
        assertEquals(galleryEntityList.size(), result.getContent().size());

        for (int i = 0; i < galleryEntityList.size(); i++) {
            Gallery entity = galleryEntityList.get(i);
            GalleryDto dto = result.getContent().get(i);

            assertEquals(entity.getPrompt(), dto.getPrompt());
            assertEquals(entity.getStorageUrl(), dto.getStorageUrl());
            assertEquals(entity.getOption(), dto.getOption());
        }
    }

    @Test
    void testGetGalleriesByTagName() {
        // Given
        String tagName = "tag";
        Pageable pageable = PageRequest.of(0, 10);
        List<Gallery> galleryEntityList = Arrays.asList(
                Gallery.create("prompt1", "url1", 1),
                Gallery.create("prompt2", "url2", 2)
        );
        Page<Gallery> galleryEntityPage = new PageImpl<>(galleryEntityList, pageable, galleryEntityList.size());

        // When
        when(galleryRepository.findByTagsName(tagName, pageable)).thenReturn(galleryEntityPage);

        // Then
        Page<GalleryDto> result = galleryService.getGalleriesByTagName(tagName, pageable);
        assertEquals(galleryEntityList.size(), result.getContent().size());

        for (int i = 0; i < galleryEntityList.size(); i++) {
            Gallery entity = galleryEntityList.get(i);
            GalleryDto dto = result.getContent().get(i);

            assertEquals(entity.getPrompt(), dto.getPrompt());
            assertEquals(entity.getStorageUrl(), dto.getStorageUrl());
            assertEquals(entity.getOption(), dto.getOption());
        }
    }
}