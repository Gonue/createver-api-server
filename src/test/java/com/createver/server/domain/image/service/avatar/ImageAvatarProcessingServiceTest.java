package com.createver.server.domain.image.service.avatar;

import com.createver.server.domain.image.dto.response.ImageAvatarWebhookResponse;
import com.createver.server.domain.image.entity.ImageAvatar;
import com.createver.server.domain.image.repository.avatar.ImageAvatarRepository;
import com.createver.server.global.util.aws.service.S3UploadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Image Avatar Processing Service 테스트")
@ExtendWith(MockitoExtension.class)
class ImageAvatarProcessingServiceTest {

    @InjectMocks
    private ImageAvatarProcessingService imageAvatarProcessingService;
    @Mock
    private ImageAvatarSseService imageAvatarSseService;

    @Mock
    private ImageAvatarRepository imageAvatarRepository;

    @Mock
    private S3UploadService s3UploadService;

    @DisplayName("Webhook 응답 처리 - 이미지 URL 있음")
    @Test
    void processWebhookResponse_WithImageUrls() {
        // Given
        ImageAvatarWebhookResponse response = new ImageAvatarWebhookResponse();
        response.setId("predictionId");
        response.setImageUrls(Arrays.asList("http://example.com/image.png"));

        ImageAvatar imageAvatar = ImageAvatar.builder()
            .predictionId("predictionId")
            .build();

        when(imageAvatarRepository.findByPredictionId(anyString())).thenReturn(imageAvatar);
        when(s3UploadService.uploadFromUrl(anyString(), anyString())).thenReturn("s3ImageUrl");

        // When
        imageAvatarProcessingService.processWebhookResponse(response);

        // Then
        verify(imageAvatarRepository, times(1)).findByPredictionId("predictionId");
        verify(s3UploadService, times(1)).uploadFromUrl("http://example.com/image.png", "image/png");
        verify(imageAvatarRepository, times(1)).save(imageAvatar);
    }

    @DisplayName("Webhook 응답 처리 - 이미지 URL 없음")
    @Test
    void processWebhookResponse_NoImageUrls() {
        // Given
        ImageAvatarWebhookResponse response = new ImageAvatarWebhookResponse();
        response.setId("predictionId");
        response.setImageUrls(Arrays.asList());

        ImageAvatar mockImageAvatar = ImageAvatar.builder()
            .predictionId("predictionId")
            .build();

        when(imageAvatarRepository.findByPredictionId("predictionId")).thenReturn(mockImageAvatar);

        // When
        imageAvatarProcessingService.processWebhookResponse(response);

        // Then
        verify(imageAvatarRepository, times(1)).findByPredictionId("predictionId");
        verify(s3UploadService, never()).uploadFromUrl(anyString(), anyString());
        verify(imageAvatarRepository, never()).save(any(ImageAvatar.class));
    }

    @DisplayName("Webhook 응답 처리 - ImageAvatar 찾을 수 없음")
    @Test
    void processWebhookResponse_ImageAvatarNotFound() {
        // Given
        ImageAvatarWebhookResponse response = new ImageAvatarWebhookResponse();
        response.setId("predictionId");
        response.setImageUrls(Arrays.asList("http://example.com/image.png"));

        when(imageAvatarRepository.findByPredictionId(anyString())).thenReturn(null);

        // When
        imageAvatarProcessingService.processWebhookResponse(response);

        // Then
        verify(imageAvatarRepository, times(1)).findByPredictionId("predictionId");
        verify(s3UploadService, never()).uploadFromUrl(anyString(), anyString());
        verify(imageAvatarRepository, never()).save(any(ImageAvatar.class));
    }

    @DisplayName("SSE 이벤트 성공적으로 전송")
    @Test
    void processWebhookResponse_WithValidImageUrls() throws IOException {
        // Given
        String predictionId = "predictionId";
        String imageUrl = "http://example.com/image.png";
        SseEmitter emitter = mock(SseEmitter.class);

        ImageAvatar imageAvatar = ImageAvatar.builder()
                .predictionId(predictionId)
                .build();

        when(imageAvatarRepository.findByPredictionId(predictionId)).thenReturn(imageAvatar);
        when(s3UploadService.uploadFromUrl(imageUrl, "image/png")).thenReturn("s3ImageUrl");
        when(imageAvatarSseService.getEmitters(predictionId)).thenReturn(Arrays.asList(emitter));

        ImageAvatarWebhookResponse response = new ImageAvatarWebhookResponse();
        response.setId(predictionId);
        response.setImageUrls(Arrays.asList(imageUrl));

        // When
        imageAvatarProcessingService.processWebhookResponse(response);

        // Then
        verify(emitter).send(any(SseEmitter.SseEventBuilder.class));
        verify(emitter).complete();
    }
}