package com.createver.server.domain.article.entity;

import com.createver.server.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("Article Entity 테스트")
class ArticleTest {

    @DisplayName("Article 생성 테스트")
    @Test
    void testArticleCreation() {
        // Given
        Member mockMember = mock(Member.class);

        // When
        Article article = Article.builder()
                .title("Test Title")
                .content("Test Content")
                .member(mockMember)
                .thumbnailUrl("Test Thumbnail URL")
                .build();

        // Then
        assertEquals("Test Title", article.getTitle());
        assertEquals("Test Content", article.getContent());
        assertEquals(mockMember, article.getMember());
        assertEquals("Test Thumbnail URL", article.getThumbnailUrl());
    }

    @DisplayName("Article 업데이트 테스트")
    @Test
    void testUpdateArticle() {
        // Given
        Member mockMember = mock(Member.class);

        Article article = Article.builder()
                .title("Original Title")
                .content("Original Content")
                .member(mockMember)
                .thumbnailUrl("Original Thumbnail URL")
                .build();

        // When
        article.updateArticle("Updated Title", "Updated Content", "Updated Thumbnail URL");

        // Then
        assertEquals("Updated Title", article.getTitle());
        assertEquals("Updated Content", article.getContent());
        assertEquals("Updated Thumbnail URL", article.getThumbnailUrl());
    }
}