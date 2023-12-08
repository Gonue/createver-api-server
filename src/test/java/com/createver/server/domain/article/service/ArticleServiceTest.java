package com.createver.server.domain.article.service;

import com.createver.server.domain.article.dto.ArticleDto;
import com.createver.server.domain.article.entity.Article;
import com.createver.server.domain.article.repository.ArticleRepository;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@DisplayName("Article Service 테스트")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleCacheService articleCacheService;

    @InjectMocks
    private ArticleService articleService;

    private Member member;

    private Article article;


    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("test@example.com")
                .nickName("TestUser")
                .password("password")
                .profileImage("url")
                .roles(List.of("ROLE_USER"))
                .build();
        article = Article.builder()
                .title("Test Title")
                .content("Test Content")
                .member(member)
                .thumbnailUrl("Test Thumbnail")
                .build();
    }

    @Test
    @DisplayName("Article 생성 테스트")
    void createArticleTest() {
        //Given
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

        // When - 테스트 대상 메서드 실행
        articleService.createArticle("Test Title", "Test Content", "user@example.com", "Test Thumbnail");

        // Then - 예상 결과 검증
        verify(articleRepository).save(any(Article.class));
        verify(articleCacheService).evictAllArticlesCache();
    }

    @Test
    @DisplayName("Article 단일 조회 테스트")
    void findArticleByIdTest() {
        // Given
        when(articleRepository.findById(anyLong())).thenReturn(Optional.of(article));

        // When
        ArticleDto result = articleService.findArticleById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Content", result.getContent());
        verify(articleRepository).findById(anyLong());
    }

    @Test
    @DisplayName("Article 목록 조회 테스트")
    void articleListTest() {
        // Given
        when(articleCacheService.getArticles(any(Pageable.class))).thenReturn(List.of(new ArticleDto()));
        when(articleCacheService.getTotalArticleCount()).thenReturn(1L);

        // When
        var result = articleService.articleList(Pageable.unpaged());

        // Then
        assertNotNull(result);
        verify(articleCacheService).getArticles(any(Pageable.class));
    }

    @Test
    @DisplayName("Article 업데이트 테스트")
    void updateArticleTest() {
        // Given
        Long articleId = 1L;
        String email = "user@example.com";
        String updatedTitle = "Updated Title";
        String updatedContent = "Updated Content";
        Member mockMember = mock(Member.class);
        Article mockArticle = mock(Article.class);

        when(mockMember.getMemberId()).thenReturn(1L);
        when(mockMember.getProfileImage()).thenReturn("mockImageUrl");
        when(mockArticle.getMember()).thenReturn(mockMember);
        when(mockArticle.getArticleId()).thenReturn(articleId);
        when(mockArticle.getTitle()).thenReturn(updatedTitle);
        when(mockArticle.getContent()).thenReturn(updatedContent);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(mockMember));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(mockArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(mockArticle);

        // When
        ArticleDto updatedArticle = articleService.updateArticle(updatedTitle, updatedContent, email, articleId, "Updated Thumbnail");

        // Then
        assertNotNull(updatedArticle);
        assertEquals(updatedTitle, updatedArticle.getTitle());
        assertEquals(updatedContent, updatedArticle.getContent());
        verify(articleRepository).save(any(Article.class));
        verify(articleCacheService).evictAllArticlesCache();
    }



    @Test
    @DisplayName("Article 삭제 테스트")
    void deleteArticleTest() {
        // Given
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
        when(articleRepository.findById(anyLong())).thenReturn(Optional.of(article));

        // When
        articleService.deleteArticle("user@example.com", 1L);

        // Then
        verify(articleRepository).delete(any(Article.class));
        verify(articleCacheService).evictAllArticlesCache();
    }

    @Test
    @DisplayName("Article 존재하지 않는 회원 조회 시 예외 발생")
    void findMemberByEmailNotFoundTest() {
        // Given
        String email = "nonexistent@example.com";
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessLogicException.class, () -> articleService.createArticle("Title", "Content", email, "Thumbnail"));
    }

    @Test
    @DisplayName("Article 존재하지 않는 게시글 조회 시 예외 발생")
    void findArticleByIdNotFoundTest() {
        // Given
        Long articleId = 999L;
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessLogicException.class, () -> articleService.findArticleById(articleId));
    }

    @Test
    @DisplayName("Article 업데이트 권한 없는 사용자 시 예외 발생")
    void updateArticleInvalidPermissionTest() {
        // Given
        Long articleId = 1L;
        String differentEmail = "another@example.com";
        Member memberWithDifferentId = mock(Member.class);
        Member originalMember = mock(Member.class); // Article의 원래 Member 객체
        Article article = mock(Article.class);

        when(memberWithDifferentId.getMemberId()).thenReturn(2L); // 다른 ID 반환
        when(originalMember.getMemberId()).thenReturn(1L); // 원래 Member의 ID 반환
        when(article.getMember()).thenReturn(originalMember); // 원래 Member 반환

        when(memberRepository.findByEmail(differentEmail)).thenReturn(Optional.of(memberWithDifferentId));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        // When & Then
        assertThrows(BusinessLogicException.class,
            () -> articleService.updateArticle("New Title", "New Content", differentEmail, articleId, "New Thumbnail"));
    }


}