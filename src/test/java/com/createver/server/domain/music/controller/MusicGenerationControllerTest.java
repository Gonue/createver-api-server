package com.createver.server.domain.music.controller;

import com.createver.server.domain.music.dto.request.MusicPromptRequest;
import com.createver.server.domain.music.dto.response.MusicWebhookResponse;
import com.createver.server.domain.music.service.MusicGenerationService;
import com.createver.server.domain.music.service.MusicProcessingService;
import com.createver.server.domain.music.service.MusicService;
import com.createver.server.global.config.SecurityConfig;
import com.createver.server.global.user.WithMockCustomMember;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MusicGenerationController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
@DisplayName("Music Generation Controller 테스트")
class MusicGenerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MusicService musicService;

    @MockBean
    private MusicGenerationService musicGenerationService;

    @MockBean
    private MusicProcessingService musicProcessingService;

    @Test
    @DisplayName("음악 생성 테스트")
    @WithMockCustomMember
    void createMusicTest() throws Exception {
        MusicPromptRequest request = MusicPromptRequest.builder()
                .prompt("test Prompt")
                .build();

        String content = objectMapper.writeValueAsString(request);

        when(musicGenerationService.generateMusic(any(), any())).thenReturn("predictionId");

        mockMvc.perform(post("/api/v1/music/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("predictionId")));
    }

    @Test
    @DisplayName("웹훅 핸들러 테스트")
    @WithMockCustomMember
    void handleWebhookTest() throws Exception {
        MusicWebhookResponse response = new MusicWebhookResponse();

        mockMvc.perform(post("/api/v1/music/create/webhook")
                        .with(csrf().asHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("음악 삭제 테스트")
    @WithMockCustomMember
    void deleteMusicTest() throws Exception {
        Long musicId = 1L;

        mockMvc.perform(delete("/api/v1/music/create/{musicId}", musicId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer {JWT_ACCESS_TOKEN}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf().asHeader())
                )
                .andExpect(status().isOk());

    }
}