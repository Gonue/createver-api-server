package com.createver.server.domain.image.controller;

import com.createver.server.domain.image.dto.request.ImageReportRequest;
import com.createver.server.domain.image.service.ImageReportService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.createver.server.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.createver.server.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Image Report Controller 테스트")
@WebMvcTest(controllers = ImageReportController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureRestDocs
class ImageReportControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ImageReportService imageReportService;


    @Test
    @DisplayName("갤러리에 신고 추가 테스트")
    @WithMockCustomMember
    void addReportTest() throws Exception {
        Long galleryId = 1L;
        ImageReportRequest reportRequest = new ImageReportRequest("신고 내용");
        String content = objectMapper.writeValueAsString(reportRequest);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/image/report/{galleryId}", galleryId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
                        .content(content)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "add-report",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("galleryId").description("신고할 갤러리의 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").description("신고 내용")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result").description("응답 결과 (신고 추가 시 별도의 결과 없음)")
                        )
                ));
    }

    @Test
    @DisplayName("갤러리의 신고 수 카운트 테스트")
    @WithMockCustomMember
    void countReportsTest() throws Exception {
        Long galleryId = 1L;
        when(imageReportService.countReportsForGallery(galleryId)).thenReturn(10L);

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/image/report/count/{galleryId}", galleryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "count-reports",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("galleryId").description("신고 수를 확인할 갤러리의 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result").description("해당 갤러리의 신고 수")
                        )
                ));
    }

    @Test
    @DisplayName("사용자의 갤러리 신고 상태 확인 테스트")
    @WithMockCustomMember
    void checkUserReportStatusTest() throws Exception {
        Long galleryId = 1L;
        when(imageReportService.hasUserReported(galleryId, "user@example.com")).thenReturn(true);

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/image/report/status/{galleryId}", galleryId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "check-user-report-status",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("galleryId").description("신고 상태를 확인할 갤러리의 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result").description("해당 사용자가 갤러리를 신고했는지 여부")
                        )
                ));
    }

    @Test
    @DisplayName("갤러리 블라인드 상태 업데이트 테스트")
    @WithMockCustomMember
    void updateGalleryBlindStatusTest() throws Exception {
        Long galleryId = 1L;
        boolean isBlinded = true;

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/image/admin/gallery/{galleryId}/blind?isBlinded={isBlinded}", galleryId, isBlinded)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "update-gallery-blind-status",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("galleryId").description("블라인드 상태를 업데이트할 갤러리의 ID")
                        ),
                        queryParameters(
                                parameterWithName("isBlinded").description("갤러리를 블라인드 처리할지 여부")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result").description("응답 결과 (블라인드 상태 업데이트 시 별도의 결과 없음)")
                        )
                ));
    }


}