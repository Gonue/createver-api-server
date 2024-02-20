package com.createver.server.domain.image.controller;

import com.createver.server.domain.image.dto.request.PromptRequest;
import com.createver.server.domain.image.dto.response.CustomGenerationResponse;
import com.createver.server.domain.image.service.gallery.ImageGenerationService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.createver.server.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.createver.server.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Image Generation Controller 테스트")
@WebMvcTest(controllers = ImageGenerationController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureRestDocs
class ImageGenerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ImageGenerationService imageGenerationService;

    @Test
    @DisplayName("일반 이미지 생성 요청 처리 테스트")
    @WithMockCustomMember
    void inputRequestTest() throws Exception {
        // given
        PromptRequest promptRequest = new PromptRequest("테스트 프롬프트", 5);
        String content = objectMapper.writeValueAsString(promptRequest);

        List<CustomGenerationResponse> customGenerationResponses = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            CustomGenerationResponse customGenerationResponse = new CustomGenerationResponse(
                    i + 1L,
                    "Test Prompt",
                    "http://image.url",
                    5,
                    LocalDateTime.now()
            );
            customGenerationResponses.add(customGenerationResponse);
        }

        when(imageGenerationService.makeImages(any(PromptRequest.class), anyString())).thenReturn(customGenerationResponses);

        // when
        ResultActions actions =
                mockMvc.perform(
                        post("/api/v1/image/create")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf().asHeader())
                                .content(content)
                );
        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "input-image-request",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("prompt").description("이미지 생성 프롬프트"),
                                fieldWithPath("option").description("이미지 생성 옵션")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                subsectionWithPath("result[]").description("생성된 이미지에 대한 정보"),
                                fieldWithPath("result[].galleryId").description("갤러리 ID"),
                                fieldWithPath("result[].prompt").description("이미지 생성에 사용된 프롬프트"),
                                fieldWithPath("result[].s3Url").description("이미지의 S3 URL"),
                                fieldWithPath("result[].option").description("이미지 생성 옵션"),
                                fieldWithPath("result[].createdAt").description("이미지 생성 시간")
                        )
                ));
    }

    @Test
    @WithMockCustomMember
    @DisplayName("Album 용 Simple 이미지 생성 요청 테스트")
    void simpleImageGenerationTest() throws Exception {
        // given
        PromptRequest promptRequest = new PromptRequest("Simple Test Prompt", 5);
        String content = objectMapper.writeValueAsString(promptRequest);

        List<String> mockS3Urls = Arrays.asList("http://example-url.com/image1.png", "http://example-url.com/image2.png");
        when(imageGenerationService.simpleImageMake(anyString())).thenReturn(mockS3Urls);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/image/create/simple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .with(csrf().asHeader())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                        "simple-image-generation",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("prompt").description("이미지 생성 프롬프트"),
                                fieldWithPath("option").description("이미지 생성 옵션").optional()
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result[]").description("생성된 이미지의 CloudFront URL 목록")
                        )
                ));
    }
}