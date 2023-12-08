package com.createver.server.domain.image.entity;

import com.createver.server.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("Image View Entity 테스트")
class ImageViewTest {

    @DisplayName("Image View 생성 테스트")
    @Test
    void testImageViewCreation() {
        // Given
        Member mockMember = mock(Member.class);
        Gallery mockGallery = mock(Gallery.class);
        LocalDateTime now = LocalDateTime.now();

        // When
        ImageView imageView = ImageView.builder()
                .member(mockMember)
                .gallery(mockGallery)
                .viewedAt(now)
                .build();

        // Then
        assertEquals(mockMember, imageView.getMember());
        assertEquals(mockGallery, imageView.getGallery());
        assertEquals(now, imageView.getViewedAt());
    }
}