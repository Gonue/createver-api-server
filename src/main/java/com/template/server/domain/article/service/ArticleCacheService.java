package com.template.server.domain.article.service;

import com.template.server.domain.article.dto.ArticleDto;
import com.template.server.domain.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleCacheService {

    private final ArticleRepository articleRepository;

    @Cacheable(value = "ArticleCacheStore", key = "#pageable", cacheManager = "cacheManager", unless = "#result == null")
    public List<ArticleDto> getArticles(Pageable pageable) {
        return articleRepository.findAll(pageable).stream()
                .map(ArticleDto::from)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "ArticleCacheStore", key = "'totalArticleCount'", cacheManager = "cacheManager")
    public long getTotalArticleCount() {
        return articleRepository.count();
    }

    @CacheEvict(value = "ArticleCacheStore", allEntries = true)
    public void evictAllArticlesCache() {
        //캐시 초기화
    }
}
