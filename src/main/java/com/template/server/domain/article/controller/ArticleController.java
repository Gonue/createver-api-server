package com.template.server.domain.article.controller;

import com.template.server.domain.article.dto.ArticleDto;
import com.template.server.domain.article.dto.request.ArticleCreateRequest;
import com.template.server.domain.article.dto.request.ArticleUpdateRequest;
import com.template.server.domain.article.dto.response.ArticleResponse;
import com.template.server.domain.article.service.ArticleService;
import com.template.server.global.error.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
public class ArticleController {

    private final ArticleService articleService;

    //Create Article
    @PostMapping
    public Response<Void> createArticle(@RequestBody ArticleCreateRequest request, Authentication authentication){
        articleService.createArticle(request.getTitle(), request.getContent(), authentication.getName());
        return Response.success(201, null);
    }

    //Single Article
    @GetMapping("/{articleId}")
    public Response<ArticleResponse> findArticleById(@PathVariable Long articleId){
        ArticleDto articleDto = articleService.findArticleById(articleId);
        return Response.success(200, ArticleResponse.from(articleDto));
    }

    //List Article
    @GetMapping
    public Response<Page<ArticleResponse>> articleList(Pageable pageable){
        return Response.success(articleService.articleList(pageable).map(ArticleResponse::from));
    }

    //Update Article
    @PatchMapping("/{articleId}")
    public Response<ArticleResponse> updateArticle(@PathVariable Long articleId,
                                                   @RequestBody ArticleUpdateRequest request,
                                                   Authentication authentication){
        ArticleDto articleDto = articleService.updateArticle(request.getTitle(), request.getContent(), authentication.getName(), articleId);
        return Response.success(200, ArticleResponse.from(articleDto));
    }

    //Delete Article
    @DeleteMapping("/{articleId}")
    public Response<Void> deleteArticle(@PathVariable Long articleId, Authentication authentication){
        articleService.deleteArticle(authentication.getName(), articleId);
        return Response.success();
    }
}
