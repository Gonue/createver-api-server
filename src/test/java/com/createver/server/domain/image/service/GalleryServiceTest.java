package com.createver.server.domain.image.service;

import com.createver.server.domain.image.dto.GalleryDto;
import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.repository.gallery.GalleryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@DisplayName("Gallery Service 테스트")
@ExtendWith(MockitoExtension.class)
class GalleryServiceTest {

    @InjectMocks
    private GalleryService galleryService;

    @Mock
    private GalleryRepository galleryRepository;

    private Pageable pageable;
    private Gallery gallery;
    private GalleryDto galleryDto;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
        gallery = Gallery.builder()
                .prompt("test prompt")
                .storageUrl("url")
                .option(1)
                .tags(new ArrayList<>())
                .member(null)
                .build();

        galleryDto = new GalleryDto(
                1L, "test prompt", "url", 1, LocalDateTime.now(),
                0L, 10, 5, 0, false
        );
    }

    @Test
    void findGalleryListByOptionsAndPromptTest() {
        // Given
        List<Integer> options = Arrays.asList(1, 2, 3);
        String prompt = "test prompt";
        Page<Gallery> page = new PageImpl<>(Collections.singletonList(gallery));
        when(galleryRepository.hasOptionsAndPrompt(options, prompt, pageable)).thenReturn(page);

        // When
        Page<GalleryDto> result = galleryService.findGalleryListByOptionsAndPrompt(options, prompt, pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(prompt, result.getContent().get(0).getPrompt());
        verify(galleryRepository).hasOptionsAndPrompt(options, prompt, pageable);
    }

    @Test
    void getGalleriesByTagNameTest() {
        // Given
        String tagName = "testTag";
        Page<Gallery> page = new PageImpl<>(Collections.singletonList(gallery));
        when(galleryRepository.findGalleryByTagsName(tagName, pageable)).thenReturn(page);

        // When
        Page<GalleryDto> result = galleryService.getGalleriesByTagName(tagName, pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.getContent().stream().allMatch(dto -> dto.getPrompt().equals("test prompt")));
        verify(galleryRepository).findGalleryByTagsName(tagName, pageable);
    }

    @Test
    void galleryListWithCommentTest() {
        // Given
        Page<GalleryDto> pageDto = new PageImpl<>(Collections.singletonList(galleryDto));
        when(galleryRepository.findAllGalleriesWithComment(pageable)).thenReturn(pageDto);

        // When
        Page<GalleryDto> result = galleryService.galleryListWithComment(pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("test prompt", result.getContent().get(0).getPrompt());
        verify(galleryRepository).findAllGalleriesWithComment(pageable);
    }
}
