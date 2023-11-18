package com.template.server.domain.image.service;

import com.template.server.domain.image.entity.ImageTag;
import com.template.server.domain.image.repository.ImageTagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ImageTagServiceTest {
    @InjectMocks
    private ImageTagService imageTagService;

    @Mock
    private ImageTagRepository imageTagRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetOrCreateTags() {
        // Given
        String[] tagNames = {"tag1", "tag2"};
        ImageTag tag1 = ImageTag.builder()
                .name("tag1")
                .build();
        ImageTag tag2 = ImageTag.builder()
                .name("tag2")
                .build();
        when(imageTagRepository.findByName("tag1")).thenReturn(Optional.of(tag1));
        when(imageTagRepository.findByName("tag2")).thenReturn(Optional.empty());
        when(imageTagRepository.save(any(ImageTag.class))).thenReturn(tag2);

        // When
        List<ImageTag> result = imageTagService.getOrCreateTags(tagNames);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(tag1, result.get(0));
        assertEquals(tag2, result.get(1));
    }
}