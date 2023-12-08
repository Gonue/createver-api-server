package com.createver.server.domain.image.entity;

import com.createver.server.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("Image Comment Entity 테스트")
class ImageCommentTest {

    @DisplayName("Image Comment 생성 테스트")
    @Test
    void testImageCommentCreation() {
        // Given
        String content = "Test Comment";
        Gallery mockGallery = mock(Gallery.class);
        Member mockMember = mock(Member.class);

        // When
        ImageComment imageComment = ImageComment.builder()
                .content(content)
                .gallery(mockGallery)
                .member(mockMember)
                .build();

        // Then
        assertEquals(content, imageComment.getContent());
        assertEquals(mockGallery, imageComment.getGallery());
        assertEquals(mockMember, imageComment.getMember());
    }

    @DisplayName("Image Comment 업데이트 테스트")
    @Test
    void testUpdateContent() {
        // Given
        ImageComment imageComment = ImageComment.builder()
                .content("Original Content")
                .gallery(mock(Gallery.class))
                .member(mock(Member.class))
                .build();

        // When
        String updatedContent = "Updated Content";
        imageComment.updateContent(updatedContent);

        // Then
        assertEquals(updatedContent, imageComment.getContent());
    }
}