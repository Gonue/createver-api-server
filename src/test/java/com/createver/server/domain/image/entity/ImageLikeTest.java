package com.createver.server.domain.image.entity;

import com.createver.server.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


@DisplayName("Image Like Entity 테스트")
class ImageLikeTest {

    @DisplayName("Image Like 생성 테스트")
    @Test
    void testImageLikeCreation() {
        // Given
        Member mockMember = mock(Member.class);
        Gallery mockGallery = mock(Gallery.class);

        // When
        ImageLike imageLike = ImageLike.builder()
                .member(mockMember)
                .gallery(mockGallery)
                .build();

        // Then
        assertEquals(mockMember, imageLike.getMember());
        assertEquals(mockGallery, imageLike.getGallery());
    }
}