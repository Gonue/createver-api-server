package com.createver.server.domain.music.service;


import com.createver.server.domain.music.dto.response.MusicWebhookResponse;
import com.createver.server.domain.music.entity.Music;
import com.createver.server.domain.music.repository.music.MusicRepository;
import com.createver.server.global.sse.SseService;
import com.createver.server.global.util.aws.service.S3UploadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Music Processing Service 테스트")
@ExtendWith(MockitoExtension.class)
class MusicProcessingServiceTest {

    @InjectMocks
    private MusicProcessingService musicProcessingService;

    @Mock
    private MusicRepository musicRepository;

    @Mock
    private S3UploadService s3UploadService;

    @Mock
    private SseService sseService;

    @Mock
    private RestTemplate restTemplate;

    @DisplayName("Webhook 응답 처리 - 음악 URL 있음")
    @Test
    void processWebhookResponse_WithMusicUrl() throws IOException {
        // Given
        MusicWebhookResponse response = new MusicWebhookResponse();
        response.setId("predictionId");
        response.setMusicUrl("http://example.com/music.wav");

        Music music = Music.builder()
            .predictionId("predictionId")
            .build();

        when(musicRepository.findByPredictionId("predictionId")).thenReturn(Optional.of(music));
        when(restTemplate.execute(anyString(), any(), any(), any())).thenReturn(new byte[]{1, 2, 3});
        when(s3UploadService.uploadWavAndReturnCloudFrontUrl(any())).thenReturn("s3CloudFrontUrl");

        // When
        musicProcessingService.processWebhookResponse(response);

        // Then
        verify(musicRepository, times(1)).findByPredictionId("predictionId");
        verify(s3UploadService, times(1)).uploadWavAndReturnCloudFrontUrl(any());
        verify(musicRepository, times(1)).save(music);
    }


    @DisplayName("SSE 이벤트 성공적으로 전송")
    @Test
    void sendSseEvent_Success() throws IOException {
        // Given
        String predictionId = "predictionId";
        String s3CloudFrontUrl = "s3CloudFrontUrl";
        SseEmitter emitter = mock(SseEmitter.class);

        Music music = Music.builder()
                .predictionId(predictionId)
                .build();

        when(musicRepository.findByPredictionId(predictionId)).thenReturn(Optional.ofNullable(music));
        when(sseService.getEmitters(predictionId)).thenReturn(Arrays.asList(emitter));

        MusicWebhookResponse response = new MusicWebhookResponse();
        response.setId(predictionId);
        response.setMusicUrl(s3CloudFrontUrl);

        // When
        musicProcessingService.processWebhookResponse(response);

        // Then
        verify(emitter).send(any(SseEmitter.SseEventBuilder.class));
        verify(emitter).complete();
    }
}
