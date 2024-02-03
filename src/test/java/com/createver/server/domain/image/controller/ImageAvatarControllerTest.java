package com.createver.server.domain.image.controller;

import com.createver.server.domain.image.dto.request.AvatarPromptRequest;
import com.createver.server.domain.image.dto.response.ImageAvatarWebhookResponse;
import com.createver.server.domain.image.service.ImageAvatarProcessingService;
import com.createver.server.domain.image.service.ImageAvatarService;
import com.createver.server.domain.image.service.ImageAvatarSseService;
import com.createver.server.global.util.aws.service.S3DownloadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Image Avatar Controller 테스트")
class ImageAvatarControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @InjectMocks
    private ImageAvatarController imageAvatarController;

    @Mock
    private ImageAvatarService imageAvatarService;

    @Mock
    private ImageAvatarProcessingService imageAvatarProcessingService;

    @Mock
    private Authentication authentication;

    @Mock
    private ImageAvatarSseService imageAvatarSseService;

    @Mock
    private S3DownloadService s3DownloadService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(imageAvatarController).build();
        objectMapper = new ObjectMapper();
    }

    @DisplayName("아바타 이미지 생성 테스트")
    @Test
    void generateAvatarTest() throws Exception {
        AvatarPromptRequest request = new AvatarPromptRequest();
        when(authentication.getName()).thenReturn("test@test.com");
        when(imageAvatarService.generateAvatarImage(any(), any())).thenReturn("predictionId");

        mockMvc.perform(post("/api/v1/image/avatar")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("predictionId")));
    }

    @DisplayName("웹훅 핸들러 테스트")
    @Test
    void handleWebhookTest() throws Exception {
        ImageAvatarWebhookResponse webhookResponse = new ImageAvatarWebhookResponse();

        mockMvc.perform(post("/api/v1/image/avatar/webhook")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(webhookResponse)))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("SSE 스트림 테스트")
    void streamTest() throws Exception {
        String id = "testId";
        mockMvc.perform(get("/api/v1/image/avatar/stream/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_EVENT_STREAM_VALUE));
    }

    @Test
    @DisplayName("이미지 다운로드 테스트 - 성공 케이스")
    void downloadImage_Success() throws Exception {
        String predictionId = "predictionId";
        byte[] fakeImageData = new byte[20];
        when(s3DownloadService.downloadImageByPredictionId(predictionId)).thenReturn(fakeImageData);

        mockMvc.perform(get("/api/v1/image/avatar/download/{predictionId}", predictionId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + predictionId + ".png\""));
    }

    @Test
    @DisplayName("이미지 다운로드 테스트 - 실패 케이스")
    void downloadImage_Failure() throws Exception {
        String predictionId = "invalidPredictionId";
        when(s3DownloadService.downloadImageByPredictionId(predictionId)).thenReturn(null);

        mockMvc.perform(get("/api/v1/image/avatar/download/{predictionId}", predictionId))
                .andExpect(status().isBadRequest());
    }
}