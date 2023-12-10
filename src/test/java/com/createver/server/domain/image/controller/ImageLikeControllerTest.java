package com.createver.server.domain.image.controller;

import com.createver.server.domain.image.service.ImageLikeService;
import com.createver.server.global.config.SecurityConfig;
import com.createver.server.global.user.WithMockCustomMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.createver.server.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.createver.server.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Image Like Controller 테스트")
@WebMvcTest(controllers = ImageLikeController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureRestDocs
class ImageLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageLikeService imageLikeService;


    @Test
    @DisplayName("갤러리에 좋아요 추가 테스트")
    @WithMockCustomMember
    void addLikeTest() throws Exception {
        Long galleryId = 1L;

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/image/like/{galleryId}", galleryId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "add-like",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("galleryId").description("좋아요를 추가할 갤러리의 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result").description("응답 결과 (좋아요 추가 시 별도의 결과 없음)")
                        )
                ));
    }


    @Test
    @DisplayName("갤러리의 좋아요 수 카운트 테스트")
    @WithMockCustomMember
    void countLikesTest() throws Exception {
        Long galleryId = 1L;
        when(imageLikeService.countLikesForGallery(galleryId)).thenReturn(10L);

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/image/like/count/{galleryId}", galleryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "count-likes",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("galleryId").description("좋아요 수를 확인할 갤러리의 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result").description("해당 갤러리의 좋아요 수")
                        )
                ));
    }

    @Test
    @DisplayName("사용자의 갤러리 좋아요 상태 확인 테스트")
    @WithMockCustomMember
    void checkUserLikeStatusTest() throws Exception {
        Long galleryId = 1L;
        when(imageLikeService.hasUserLiked(galleryId, "user@example.com")).thenReturn(true);

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/image/like/status/{galleryId}", galleryId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "check-user-like-status",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("galleryId").description("좋아요 상태를 확인할 갤러리의 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result").description("해당 사용자가 갤러리에 좋아요를 눌렀는지 여부")
                        )
                ));
    }

}