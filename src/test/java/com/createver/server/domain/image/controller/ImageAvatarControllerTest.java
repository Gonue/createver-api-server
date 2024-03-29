package com.createver.server.domain.image.controller;

import com.createver.server.domain.image.dto.request.AvatarPromptRequest;
import com.createver.server.domain.image.dto.response.ImageAvatarWebhookResponse;
import com.createver.server.domain.image.service.avatar.ImageAvatarProcessingService;
import com.createver.server.domain.image.service.avatar.ImageAvatarGenerationService;
import com.createver.server.global.sse.SseService;
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
    private ImageAvatarGenerationService imageAvatarGenerationService;

    @Mock
    private ImageAvatarProcessingService imageAvatarProcessingService;

    @Mock
    private Authentication authentication;

    @Mock
    private SseService sseService;

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
        AvatarPromptRequest request = AvatarPromptRequest.builder()
                .prompt("Test prompt")
                .numSteps(10)
                .styleName("Test style")
                .inputImage("Test image")
                .numOutputs(2)
                .guidanceScale(5)
                .negativePrompt("Test negative prompt")
                .styleStrengthRatio(20)
                .build();

        when(authentication.getName()).thenReturn("test@test.com");
        when(imageAvatarGenerationService.generateAvatarImage(any(), any())).thenReturn("predictionId");

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