package com.createver.server.domain.article.controller;

import com.createver.server.domain.article.dto.ArticleDto;
import com.createver.server.domain.article.dto.request.ArticleCreateRequest;
import com.createver.server.domain.article.dto.request.ArticleUpdateRequest;
import com.createver.server.domain.article.service.ArticleService;
import com.createver.server.domain.member.dto.MemberDto;
import com.createver.server.domain.member.entity.PlanType;
import com.createver.server.global.config.SecurityConfig;
import com.createver.server.global.user.WithMockCustomMember;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.createver.server.global.util.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Article Controller 테스트")
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

    @DisplayName("게시글 작성")
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
                                        fieldWithPath("title").description("제목"),
                                        fieldWithPath("content").description("내용"),
                                        fieldWithPath("thumbnailUrl").description("썸네일 URL")
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
    @DisplayName("게시글 수정")
    @WithMockCustomMember
    void updateArticleTest() throws Exception {
        // given
        Long articleId = 1L;
        ArticleUpdateRequest request = new ArticleUpdateRequest("Updated Title", "Updated Content", "Updated ThumbnailUrl");
        String content = objectMapper.writeValueAsString(request);

        MemberDto mockMember = new MemberDto(1L, "test@example.com", "nickname", "password", "profileImageUrl", LocalDateTime.now(), LocalDateTime.now(), PlanType.PRO);
        ArticleDto articleDto = new ArticleDto(articleId, request.getTitle(), request.getContent(), mockMember, request.getThumbnailUrl(), LocalDateTime.now(), LocalDateTime.now());
        when(articleService.updateArticle(anyString(), anyString(), anyString(), anyLong(), anyString())).thenReturn(articleDto);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/article/admin/{articleId}", articleId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
                        .content(content)
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document(
                        "article-update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("articleId").description("수정할 게시글 ID")
                        ),
                        requestFields(
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("thumbnailUrl").description("썸네일 URL")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result.articleId").description("게시글 ID"),
                                fieldWithPath("result.title").description("게시글 제목"),
                                fieldWithPath("result.content").description("게시글 내용"),
                                fieldWithPath("result.thumbnailUrl").description("썸네일 URL"),
                                fieldWithPath("result.createdAt").description("생성 시간"),
                                fieldWithPath("result.modifiedAt").description("수정 시간"),
                                fieldWithPath("result.member.memberId").description("멤버 ID"),
                                fieldWithPath("result.member.email").description("멤버 이메일"),
                                fieldWithPath("result.member.nickName").description("멤버 닉네임"),
                                fieldWithPath("result.member.profileImage").description("멤버 프로필 이미지"),
                                fieldWithPath("result.member.planType").description("멤버 플랜 타입")
                        )
                ));
    }

    @Test
    @DisplayName("게시글 삭제")
    @WithMockCustomMember
    void deleteArticleTest() throws Exception {
        // given
        Long articleId = 1L;
        doNothing().when(articleService).deleteArticle(anyString(), anyLong());

        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/article/admin/{articleId}", articleId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document(
                        "article-delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("articleId").description("삭제할 게시글 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result").description("응답 결과")
                        )
                ));
    }

    @Test
    @DisplayName("게시글 단건 조회")
    @WithMockCustomMember
    void findArticleByIdTest() throws Exception {
        // given
        Long articleId = 1L;
        MemberDto mockMember = new MemberDto(1L, "test@example.com", "nickname", "password", "profileImageUrl", LocalDateTime.now(), LocalDateTime.now(), null);
        ArticleDto articleDto = new ArticleDto(articleId, "Title", "Content", mockMember, "ThumbnailUrl", LocalDateTime.now(), LocalDateTime.now());
        when(articleService.findArticleById(articleId)).thenReturn(articleDto);

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/article/{articleId}", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document(
                        "article-find-by-id",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("articleId").description("조회할 게시글 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result.articleId").description("게시글 ID"),
                                fieldWithPath("result.title").description("게시글 제목"),
                                fieldWithPath("result.content").description("게시글 내용"),
                                fieldWithPath("result.thumbnailUrl").description("썸네일 URL"),
                                fieldWithPath("result.createdAt").description("생성 시간"),
                                fieldWithPath("result.modifiedAt").description("수정 시간"),
                                fieldWithPath("result.member.memberId").description("멤버 ID"),
                                fieldWithPath("result.member.email").description("멤버 이메일"),
                                fieldWithPath("result.member.nickName").description("멤버 닉네임"),
                                fieldWithPath("result.member.profileImage").description("멤버 프로필 이미지"),
                                fieldWithPath("result.member.planType").description("멤버 플랜 타입")
                        )
                ));
    }


    @Test
    @DisplayName("게시글 목록 조회 및 페이징")
    @WithMockCustomMember
    void articleListTest() throws Exception {
        // given
        List<ArticleDto> articles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ArticleDto articleDto = new ArticleDto(
                    i + 1L,
                    "Test Title " + i,
                    "Test Content " + i,
                    new MemberDto(i + 1L, "test" + i + "@example.com", "김테스트", "password", "imageUrl", LocalDateTime.now(), LocalDateTime.now(), null), // member
                    "http://thumbnail.url",
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            articles.add(articleDto);
        }
        Page<ArticleDto> page = new PageImpl<>(articles, PageRequest.of(0, 20), 1);
        when(articleService.articleList(any(Pageable.class))).thenReturn(page);

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/article")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document(
                                "article-list",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                queryParameters(
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("size").description("페이지 크기"),
                                        parameterWithName("sort").description("정렬 기준 (예: [createdAt, desc], [title, asc])")
                                ),
                                responseFields(
                                        fieldWithPath("status").description("응답 상태 코드"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        subsectionWithPath("result").description("페이징 처리된 게시글 목록과 관련된 결과 데이터"),
                                        fieldWithPath("result.content[]").description("게시글 목록"),
                                        fieldWithPath("result.content[].articleId").description("게시글 ID"),
                                        fieldWithPath("result.content[].title").description("게시글 제목"),
                                        fieldWithPath("result.content[].content").description("게시글 내용"),
                                        fieldWithPath("result.content[].thumbnailUrl").description("게시글 썸네일 URL"),
                                        fieldWithPath("result.content[].createdAt").description("게시글 생성 시간"),
                                        fieldWithPath("result.content[].modifiedAt").description("게시글 수정 시간"),
                                        subsectionWithPath("result.content[].member").description("게시글 작성자 정보"),
                                        subsectionWithPath("result.pageable").description("페이지 정보"),
                                        fieldWithPath("result.totalPages").description("전체 페이지 수"),
                                        fieldWithPath("result.totalElements").description("전체 게시글 수"),
                                        fieldWithPath("result.size").description("페이지당 게시글 수"),
                                        fieldWithPath("result.number").description("현재 페이지 번호"),
                                        subsectionWithPath("result.sort").description("정렬 정보"),
                                        fieldWithPath("result.numberOfElements").description("현재 페이지의 게시글 수"),
                                        fieldWithPath("result.empty").description("결과가 비어 있는지 여부")
                                )
                        )
                );
    }
}