package com.createver.server.domain.image.service;

import com.createver.server.domain.image.dto.ImageTagDto;
import com.createver.server.domain.image.entity.ImageTag;
import com.createver.server.domain.image.repository.tag.ImageTagRepository;
import com.createver.server.domain.image.service.tag.ImageTagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@DisplayName("Image Tag Service 테스트")
@ExtendWith(MockitoExtension.class)
class ImageTagServiceTest {

    @InjectMocks
    private ImageTagService imageTagService;

    @Mock
    private ImageTagRepository imageTagRepository;

    @DisplayName("태그 생성")
    @Test
    void getOrCreateTagsTest() {
        // Given
        String[] tagNames = {"tag1", "tag2", "newTag"};
        List<ImageTag> existingTags = new ArrayList<>(Arrays.asList(
            ImageTag.builder().name("tag1").build(),
            ImageTag.builder().name("tag2").build()
        ));

        when(imageTagRepository.findByNameIn(anySet())).thenReturn(existingTags);
        when(imageTagRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        List<ImageTag> tags = imageTagService.getOrCreateTags(tagNames);

        // Then
        assertNotNull(tags);
        assertEquals(3, tags.size());
        verify(imageTagRepository).findByNameIn(anySet());
        verify(imageTagRepository).saveAll(anyList());
    }

    @DisplayName("전체 태그 목록")
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