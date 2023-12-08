package com.createver.server.domain.image.entity;

import com.createver.server.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("Gallery Entity 테스트")
class GalleryTest {

    @DisplayName("Gallery 생성 테스트")
    @Test
    void testGalleryCreation() {
        // Given
        Member mockMember = mock(Member.class);

        // When
        Gallery gallery = Gallery.builder()
                .prompt("Test Prompt")
                .storageUrl("Test Storage URL")
                .option(1)
                .tags(Collections.emptyList())
                .member(mockMember)
                .build();

        // Then
        assertEquals("Test Prompt", gallery.getPrompt());
        assertEquals("Test Storage URL", gallery.getStorageUrl());
        assertEquals(1, gallery.getOption());
        assertTrue(gallery.getTags().isEmpty());
        assertEquals(mockMember, gallery.getMember());
    }

    @DisplayName("Gallery 다운로드 수 증가 테스트")
    @Test
    void testIncreaseDownloadCount() {
        // Given
        Gallery gallery = Gallery.builder()
                .prompt("Prompt")
                .storageUrl("URL")
                .option(1)
                .tags(null)
                .member(null)
                .build();
        // When
        gallery.increaseDownloadCount();

        // Then
        assertEquals(1, gallery.getDownloadCount());
    }

    @DisplayName("Gallery 좋아요 수 증가 테스트")
    @Test
    void testIncreaseLikeCount() {
        // Given
        Gallery gallery = Gallery.builder()
                .prompt("Prompt")
                .storageUrl("URL")
                .option(1)
                .tags(null)
                .member(null)
                .build();

        // When
        gallery.increaseLikeCount();

        // Then
        assertEquals(1, gallery.getLikeCount());
    }

    @DisplayName("Gallery 신고 수 증가 및 차단 테스트")
    @Test
    void testIncreaseReportCount() {
        // Given
        Gallery gallery = Gallery.builder()
                .prompt("Prompt")
                .storageUrl("URL")
                .option(1)
                .tags(null)
                .member(null)
                .build();

        // When
        for (int i = 0; i < 6; i++) {
            gallery.increaseReportCount();
        }

        // Then
        assertEquals(6, gallery.getReportCount());
        assertTrue(gallery.isBlinded());
    }

    @DisplayName("Gallery 차단 상태 업데이트 테스트")
    @Test
    void testUpdateBlindStatus() {
        // Given
        Gallery gallery = Gallery.builder()
                .prompt("Prompt")
                .storageUrl("URL")
                .option(1)
                .tags(null)
                .member(null)
                .build();
        assertFalse(gallery.isBlinded());

        // When
        gallery.updateBlindStatus(true);

        // Then
        assertTrue(gallery.isBlinded());
    }
}