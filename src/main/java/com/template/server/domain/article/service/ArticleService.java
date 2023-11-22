package com.template.server.domain.article.service;

import com.template.server.domain.article.dto.ArticleDto;
import com.template.server.domain.article.entity.Article;
import com.template.server.domain.article.repository.ArticleRepository;
import com.template.server.domain.member.entity.Member;
import com.template.server.domain.member.repository.MemberRepository;
import com.template.server.global.error.exception.BusinessLogicException;
import com.template.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCacheService articleCacheService;


    //Create Article
    @Transactional
    public void createArticle(String title, String content, String email, String thumbnailUrl){
        Member member = findMemberByEmail(email);
        Article article = Article.builder()
                .title(title)
                .content(content)
                .member(member)
                .thumbnailUrl(thumbnailUrl)
                .build();
        articleRepository.save(article);
        articleCacheService.evictAllArticlesCache();
    }

    //Single Article
    @Transactional(readOnly = true)
    public ArticleDto findArticleById(Long articleId){
        Article article = findArticle(articleId);
        return ArticleDto.from(article);
    }

    //List Article
    @Transactional(readOnly = true)
    public Page<ArticleDto> articleList(Pageable pageable) {
        List<ArticleDto> articles = articleCacheService.getArticles(pageable);
        long total = articleCacheService.getTotalArticleCount();
        return new PageImpl<>(articles, pageable, total);
    }

    //Update Article
    @Transactional
    public ArticleDto updateArticle(String title, String content, String email, Long articleId, String thumbnailUrl) {
        Member member = findMemberByEmail(email);
        Article article = findArticle(articleId);
        checkArticleMember(article, member, email, articleId);
        article.updateArticle(title, content, thumbnailUrl);
        ArticleDto updatedArticle = ArticleDto.from(articleRepository.save(article));
        articleCacheService.evictAllArticlesCache();
        return updatedArticle;
    }

    //Delete Article
    @Transactional
    public void deleteArticle(String email, Long articleId){
        Member member = findMemberByEmail(email);
        Article article = findArticle(articleId);
        checkArticleMember(article, member, email, articleId);
        articleRepository.delete(article);
        articleCacheService.evictAllArticlesCache();
    }

    //Member Verification
    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND, String.format("%s 을 찾을수 없음", email)));
    }

    private Article findArticle(Long articleId){
        return articleRepository.findById(articleId).orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.ARTICLE_NOT_FOUND, String.format("$s 을 찾을 수 없음", articleId)));
    }

    private void checkArticleMember(Article article, Member member, String email, Long articleId) {
        if (!Objects.equals(article.getMember().getMemberId(), member.getMemberId())) {
            throw new BusinessLogicException(ExceptionCode.INVALID_PERMISSION, String.format("%s 는 %s 의 권한이 없습니다.", email, articleId));
        }
    }
}
