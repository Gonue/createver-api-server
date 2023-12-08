package com.createver.server.domain.image.entity;

import com.createver.server.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("Image Report Entity 테스트")
class ImageReportTest {

    @DisplayName("Image Report 생성 테스트")
    @Test
    void testImageReportCreation() {
        // Given
        String content = "Report Content";
        Gallery mockGallery = mock(Gallery.class);
        Member mockMember = mock(Member.class);

        // When
        ImageReport imageReport = ImageReport.builder()
                .content(content)
                .gallery(mockGallery)
                .member(mockMember)
                .build();

        // Then
        assertEquals(content, imageReport.getContent());
        assertEquals(mockGallery, imageReport.getGallery());
        assertEquals(mockMember, imageReport.getMember());
    }
}