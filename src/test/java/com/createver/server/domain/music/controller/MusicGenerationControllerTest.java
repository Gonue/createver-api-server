package com.createver.server.domain.music.controller;

import com.createver.server.domain.music.dto.request.MusicPromptRequest;
import com.createver.server.domain.music.service.MusicGenerationService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Music Generation Controller 테스트")
@WebMvcTest(controllers = MusicGenerationController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@AutoConfigureRestDocs
class MusicGenerationControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MusicGenerationService musicGenerationService;

    @Test
    @DisplayName("음악 생성 요청 처리 테스트")
    @WithMockCustomMember
    void createMusicTest() throws Exception {
        // given
        MusicPromptRequest request = new MusicPromptRequest("테스트 프롬프트");
        String content = objectMapper.writeValueAsString(request);
        when(musicGenerationService.generateAndUploadMusic(any(MusicPromptRequest.class))).thenReturn("http://generated.music.url");

        // when
        ResultActions actions =
                mockMvc.perform(
                post("/api/v1/music/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
                        .content(content)
        );

        // then
        actions.
                andExpect(status().isOk())
                .andDo(document(
                        "create-music",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("prompt").description("음악 생성을 위한 프롬프트")
                        ),
                        responseFields(
                                fieldWithPath("status").description("응답 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("result").description("생성된 음악의 URL")
                        )
                ));
    }
}