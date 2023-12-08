package com.createver.server.domain.article.controller;

import com.createver.server.domain.article.dto.ArticleDto;
import com.createver.server.domain.article.dto.request.ArticleCreateRequest;
import com.createver.server.domain.article.service.ArticleService;
import com.createver.server.domain.member.dto.MemberDto;
import com.createver.server.global.config.SecurityConfig;
import com.createver.server.global.user.WithMockCustomMember;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.createver.server.global.util.ApiDocumentUtils.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ArticleController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureRestDocs
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockBean
    private ArticleService articleService;

    @Test
    @WithMockCustomMember
    void createArticleTest() throws Exception {
        //given
        ArticleCreateRequest request = new ArticleCreateRequest("Title", "Content", "ThumbnailUrl");
        String content = objectMapper.writeValueAsString(request);


        //when
        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/article/admin")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf().asHeader())
                                .content(content)
                );

        //then
        actions
                .andExpect(status().isOk())

                .andDo(document(
                                "article-create",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("title").description("The title of the article"),
                                        fieldWithPath("content").description("The content of the article"),
                                        fieldWithPath("thumbnailUrl").description("The thumbnail URL of the article")
                                ),
                                responseFields(
                                        fieldWithPath("status").description("응답 상태 코드"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("result").description("응답 결과")
                                )
                        )

                );
    }


    @Test
    @WithMockCustomMember
    void articleListTest() throws Exception {
        ArticleDto articleDto = new ArticleDto(
                1L, // articleId
                "Test Title", // title
                "Test Content", // content
                new MemberDto(1L, "test@example.com", "김호야", "password", "imageUrl", LocalDateTime.now(), LocalDateTime.now(), null), // member
                "http://thumbnail.url", // thumbnailUrl
                LocalDateTime.now(), // createdAt
                LocalDateTime.now() // modifiedAt
        );

        List<ArticleDto> articles = Collections.singletonList(articleDto);
        Page<ArticleDto> page = new PageImpl<>(articles, PageRequest.of(0, 10), 1);

        // ArticleService 모의 설정
        when(articleService.articleList(PageRequest.of(0, 10))).thenReturn(page);

        // API 호출 및 검증
        mockMvc.perform(get("/api/v1/article")
                        .param("page", "0")
                        .param("size", "10")
                        .with(csrf())
                )

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].title").value("Test Title"))
                .andExpect(jsonPath("$.result.content[0].content").value("Test Content"))
                .andExpect(jsonPath("$.result.content[0].member.email").value("test@example.com"))
                .andExpect(jsonPath("$.result.content[0].thumbnailUrl").value("http://thumbnail.url"))
                .andDo(document("article-list"));
    }
}