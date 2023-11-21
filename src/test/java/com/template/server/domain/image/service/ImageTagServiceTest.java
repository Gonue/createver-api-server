package com.template.server.domain.image.service;

import com.template.server.domain.image.dto.ImageTagDto;
import com.template.server.domain.image.entity.ImageTag;
import com.template.server.domain.image.repository.tag.ImageTagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageTagServiceTest {

    @InjectMocks
    private ImageTagService imageTagService;

    @Mock
    private ImageTagRepository imageTagRepository;

    @Test
    void getOrCreateTagsTest() {
        // Given
        String[] tagNames = {"tag1", "tag2", "newTag"};
        List<ImageTag> existingTags = Arrays.asList(
                ImageTag.builder().name("tag1").build(),
                ImageTag.builder().name("tag2").build()
        );

        when(imageTagRepository.findByNameIn(anySet())).thenReturn(existingTags);
        when(imageTagRepository.save(any(ImageTag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        List<ImageTag> tags = imageTagService.getOrCreateTags(tagNames);

        // Then
        assertNotNull(tags);
        assertEquals(3, tags.size());
        verify(imageTagRepository).findByNameIn(anySet());
        verify(imageTagRepository, times(1)).save(any(ImageTag.class));
    }

    @Test
    void getAllTagsTest() {
        // Given
        Pageable pageable = mock(Pageable.class);
        ImageTag imageTag = ImageTag.builder().name("name").build();
        List<ImageTag> imageTags = Collections.singletonList(imageTag);
        Page<ImageTag> imageTagPage = new PageImpl<>(imageTags, pageable, imageTags.size());

        when(imageTagRepository.findAll(pageable)).thenReturn(imageTagPage);

        // When
        Page<ImageTagDto> result = imageTagService.getAllTags(pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(imageTagRepository).findAll(pageable);
    }
}