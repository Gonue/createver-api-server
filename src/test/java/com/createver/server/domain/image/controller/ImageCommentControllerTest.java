package com.createver.server.domain.image.controller;

import com.createver.server.domain.image.dto.ImageCommentDto;
import com.createver.server.domain.image.dto.request.ImageCommentRequest;
import com.createver.server.domain.image.service.ImageCommentService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.createver.server.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.createver.server.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Image Comment Controller 테스트")
@WebMvcTest(controllers = ImageCommentController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureRestDocs
class ImageCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageCommentService imageCommentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("갤러리에 댓글 생성 테스트")
    @WithMockCustomMember
    void createCommentTest() throws Exception {
        // given
        ImageCommentRequest commentRequest = new ImageCommentRequest("테스트 댓글");
        String content = objectMapper.writeValueAsString(commentRequest);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/image/{galleryId}/comment", 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .with(csrf().asHeader())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "create-image-comment",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("galleryId").description("갤러리 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").description("댓글 내용")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result").description("응답 결과")
                        )
                ));
    }

    @Test
    @DisplayName("갤러리 댓글 수정 테스트")
    @WithMockCustomMember
    void updateCommentTest() throws Exception {
        // given
        Long imageCommentId = 1L;
        ImageCommentRequest updateRequest = new ImageCommentRequest("수정된 댓글 내용");
        String content = objectMapper.writeValueAsString(updateRequest);

        MemberDto mockMember = new MemberDto(1L, "test@example.com", "nickname", "password", "profileImageUrl", LocalDateTime.now(), LocalDateTime.now(), PlanType.PRO);

        ImageCommentDto imageCommentDto = new ImageCommentDto(
                imageCommentId,
                "테스트 댓글 수정",
                2L,
                mockMember,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(imageCommentService.updateComment(anyString(), anyLong(), anyString())).thenReturn(imageCommentDto);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/image/comment/{imageCommentId}", imageCommentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .with(csrf().asHeader())
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document(
                        "update-image-comment",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("imageCommentId").description("댓글 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").description("댓글 내용")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result.imageCommentId").description("댓글 ID"),
                                fieldWithPath("result.galleryId").description("부모 갤러리 ID"),
                                fieldWithPath("result.content").description("댓글 내용"),
                                fieldWithPath("result.createdAt").description("댓글 생성 시간"),
                                fieldWithPath("result.modifiedAt").description("댓글 수정 시간"),
                                fieldWithPath("result.member.memberId").description("멤버 ID"),
                                fieldWithPath("result.member.email").description("멤버 이메일"),
                                fieldWithPath("result.member.nickName").description("멤버 닉네임"),
                                fieldWithPath("result.member.profileImage").description("멤버 프로필 이미지"),
                                fieldWithPath("result.member.planType").description("멤버 플랜 타입")
                        )
                ));
    }

    @Test
    @DisplayName("갤러리 댓글 삭제 테스트")
    @WithMockCustomMember
    void deleteCommentTest() throws Exception {
        // given
        Long imageCommentId = 1L;
        doNothing().when(imageCommentService).deleteComment(anyString(), anyLong());

        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/image/comment/{imageCommentId}", imageCommentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "delete-image-comment",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("imageCommentId").description("삭제할 댓글의 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result").description("응답 결과 (null for delete operations)")
                        )
                ));
    }

    @Test
    @DisplayName("갤러리별 댓글 목록 조회 테스트")
    @WithMockCustomMember
    void getAllCommentsByGalleryIdTest() throws Exception {
        // given
        Long galleryId = 1L;

        MemberDto mockMember = new MemberDto(1L, "test@example.com", "nickname", "password", "profileImageUrl", LocalDateTime.now(), LocalDateTime.now(), null);
        List<ImageCommentDto> comments = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ImageCommentDto imageCommentDto = new ImageCommentDto(
                    i + 1L,
                    "Test Content " + 1,
                    galleryId,
                    mockMember,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            comments.add(imageCommentDto);
        }

        Page<ImageCommentDto> page = new PageImpl<>(comments, PageRequest.of(0, 20), 1);
        when(imageCommentService.getAllCommentsByGalleryId(eq(galleryId), any(Pageable.class))).thenReturn(page);

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/image/{galleryId}/comments", galleryId)
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "get-all-comments-by-gallery-id",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("galleryId").description("조회할 갤러리의 ID")
                        ),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기"),
                                parameterWithName("sort").description("정렬 기준 (예: [createdAt, desc], [content, asc])")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                subsectionWithPath("result").description("페이징 처리된 댓글 목록과 관련된 결과 데이터"),
                                subsectionWithPath("result.content[]").description("댓글 목록"),
                                fieldWithPath("result.content[].imageCommentId").description("댓글 ID"),
                                fieldWithPath("result.content[].galleryId").description("갤러리 ID"),
                                fieldWithPath("result.content[].content").description("댓글 내용"),
                                fieldWithPath("result.content[].createdAt").description("댓글 생성 시간"),
                                fieldWithPath("result.content[].modifiedAt").description("댓글 수정 시간"),
                                fieldWithPath("result.content[].member").description("댓글 작성자 정보"),
                                fieldWithPath("result.content[].member.memberId").description("멤버 ID"),
                                fieldWithPath("result.content[].member.email").description("멤버 이메일"),
                                fieldWithPath("result.content[].member.nickName").description("멤버 닉네임"),
                                fieldWithPath("result.content[].member.profileImage").description("멤버 프로필 이미지"),
                                fieldWithPath("result.content[].member.planType").description("멤버 플랜 타입"),
                                subsectionWithPath("result.pageable").description("페이지 정보"),
                                fieldWithPath("result.totalPages").description("전체 페이지 수"),
                                fieldWithPath("result.totalElements").description("전체 게시글 수"),
                                fieldWithPath("result.size").description("페이지당 게시글 수"),
                                fieldWithPath("result.number").description("현재 페이지 번호"),
                                subsectionWithPath("result.sort").description("정렬 정보"),
                                fieldWithPath("result.numberOfElements").description("현재 페이지의 게시글 수"),
                                fieldWithPath("result.empty").description("결과가 비어 있는지 여부")
                        )
                ));
    }


}