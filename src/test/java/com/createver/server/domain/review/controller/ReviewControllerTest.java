package com.createver.server.domain.review.controller;

import com.createver.server.domain.member.dto.MemberDto;
import com.createver.server.domain.review.dto.ReviewDto;
import com.createver.server.domain.review.dto.request.ReviewCreateRequest;
import com.createver.server.domain.review.service.ReviewService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.createver.server.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.createver.server.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Review Controller 테스트")
@WebMvcTest(controllers = ReviewController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureRestDocs
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @Test
    @DisplayName("리뷰 생성")
    @WithMockCustomMember
    void createReviewTest() throws Exception {
        //given
        ReviewCreateRequest request = new ReviewCreateRequest(5.0, "test review");
        String content = objectMapper.writeValueAsString(request);

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
                        .content(content)
        );

        //then
        actions.andExpect(status().isCreated())
                .andDo(document(
                        "review-create",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("rating").description("평점"),
                                fieldWithPath("content").description("리뷰 내용")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result").description("응답 결과")
                        )
                ));
    }

    @Test
    @DisplayName("리뷰 목록 조회")
    @WithMockCustomMember
    void reviewListTest() throws Exception {
        // given

        List<ReviewDto> reviews = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            ReviewDto reviewDto = new ReviewDto(
                    i + 1L,
                    5.0,
                    "test review",
                    new MemberDto(i + 1L, "test@mail.com" + i + "@example.com", "김테스트", "password", "imageUrl", LocalDateTime.now(), LocalDateTime.now(), null),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            reviews.add(reviewDto);
        }
        Page<ReviewDto> page = new PageImpl<>(reviews, PageRequest.of(0,20),1);
        when(reviewService.reviewList(any(Pageable.class))).thenReturn(page);

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/review/admin")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document(
                        "review-list",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                subsectionWithPath("result").description("페이징 처리된 리뷰 목록과 관련된 결과 데이터"),
                                fieldWithPath("result.content[]").description("리뷰 목록"),
                                fieldWithPath("result.content[].reviewId").description("리뷰 ID"),
                                fieldWithPath("result.content[].rating").description("평점"),
                                fieldWithPath("result.content[].content").description("리뷰 내용"),
                                fieldWithPath("result.content[].createdAt").description("생성 시간"),
                                subsectionWithPath("result.content[].member").description("리뷰 작성자 정보"),
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

    @Test
    @DisplayName("리뷰 삭제")
    @WithMockCustomMember
    void deleteReviewTest() throws Exception {
        // given
        Long reviewId = 1L;
        doNothing().when(reviewService).deleteReview(anyLong());

        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/review/admin/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        );

        // then
        actions.andExpect(status().isOk())
                .andDo(document(
                        "review-delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("reviewId").description("삭제할 리뷰 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result").description("응답 결과")
                        )
                ));
    }
}
