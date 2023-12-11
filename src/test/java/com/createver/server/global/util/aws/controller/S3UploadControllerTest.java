package com.createver.server.global.util.aws.controller;

import com.createver.server.global.config.SecurityConfig;
import com.createver.server.global.user.WithMockCustomMember;
import com.createver.server.global.util.aws.service.S3UploadService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import static com.createver.server.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.createver.server.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("S3 Upload Controller 테스트")
@WebMvcTest(controllers = S3UploadController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureRestDocs
class S3UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private S3UploadService s3UploadService;

    @Test
    @DisplayName("파일 업로드 s3 Url 반환 테스트")
    @WithMockCustomMember
    void uploadFileTest() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "images",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "test image content".getBytes()
        );

        when(s3UploadService.upload(any(MultipartFile.class))).thenReturn("http://uploaded.url");

        // when
        ResultActions actions = mockMvc.perform(
                multipart("/api/v1/image/upload")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .with(csrf().asHeader())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                                "upload-file",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestParts(
                                        partWithName("images").description("업로드할 파일")
                                ),
                                responseFields(
                                        fieldWithPath("status").description("응답 상태 코드"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("result").description("업로드된 파일의 URL")
                                )
                        )
                );
    }

    @Test
    @DisplayName("파일 업로드 cdn Url 반환 테스트")
    @WithMockCustomMember
    void uploadAndReturnCloudFrontUrl() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "images",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "test image content".getBytes()
        );

        when(s3UploadService.uploadAndReturnCloudFrontUrl(any(MultipartFile.class))).thenReturn("http://uploaded.url");

        // when
        ResultActions actions = mockMvc.perform(
                multipart("/api/v1/image/upload/cdn")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .with(csrf().asHeader())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andDo(document(
                                "upload-file-cdn",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestParts(
                                        partWithName("images").description("업로드할 파일")
                                ),
                                responseFields(
                                        fieldWithPath("status").description("응답 상태 코드"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("result").description("업로드된 파일의 URL")
                                )
                        )
                );
    }
}