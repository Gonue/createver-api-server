package com.createver.server.domain.music.service;

import static org.junit.jupiter.api.Assertions.*;

import com.createver.server.domain.music.dto.request.MusicPromptRequest;
import com.createver.server.domain.music.dto.response.MusicGenerationResponse;
import com.createver.server.global.util.aws.service.S3UploadService;
import com.createver.server.global.util.translate.Translate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Music Generation Service 테스트")
@ExtendWith(MockitoExtension.class)
class MusicGenerationServiceTest {

    @InjectMocks
    private MusicGenerationService musicGenerationService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private S3UploadService s3UploadService;

    @Mock
    private Translate translate;

    private String sageMakerEndPoint;


@DisplayName("음악 생성 및 S3 업로드 성공 테스트")
   @Test
   void generateAndUploadMusic_Success() throws Exception {
       // Given
       MusicPromptRequest request = new MusicPromptRequest("Test Prompt");
       Map<String, Object> apiResponse = new HashMap<>();
       apiResponse.put("id", "predictionId");

       MusicGenerationResponse mockResponse = new MusicGenerationResponse();
       mockResponse.setStatus("succeeded");
       mockResponse.setOutput("mockUrl");

       when(restTemplate.postForObject(eq(sageMakerEndPoint), any(HttpEntity.class), eq(Map.class))).thenReturn(apiResponse);
       when(restTemplate.exchange(anyString(), any(), any(), eq(MusicGenerationResponse.class)))
               .thenReturn(ResponseEntity.ok(mockResponse));
       when(restTemplate.execute(anyString(), any(), any(), any())).thenReturn(new byte[]{});
       when(s3UploadService.uploadWavAndReturnCloudFrontUrl(any())).thenReturn("cloudFrontUrl");

       // When
       CompletableFuture<String> futureResult = musicGenerationService.generateAndUploadMusic(request);
       String resultUrl = futureResult.join();

       // Then
       assertNotNull(resultUrl);
       assertEquals("cloudFrontUrl", resultUrl);
   }

    @DisplayName("API 응답이 null이거나 id가 없는 경우 예외 발생 테스트")
    @Test
    void generateAndUploadMusic_ApiResponseNull_ThrowsException() {
        MusicPromptRequest request = new MusicPromptRequest("Test Prompt");

        when(restTemplate.postForObject(eq(sageMakerEndPoint), any(HttpEntity.class), eq(Map.class))).thenReturn(null);

        CompletableFuture<String> futureResult = musicGenerationService.generateAndUploadMusic(request);
        assertThrows(CompletionException.class, futureResult::join);
    }


@DisplayName("API 상태가 failed 또는 canceled일 때 예외 발생 테스트")
   @Test
   void generateAndUploadMusic_FailedOrCanceled_ThrowsException() {
       MusicPromptRequest request = new MusicPromptRequest("Test Prompt");
       Map<String, Object> apiResponse = new HashMap<>();
       apiResponse.put("id", "predictionId");

       MusicGenerationResponse failedResponse = new MusicGenerationResponse();
       failedResponse.setStatus("failed");

       when(restTemplate.postForObject(eq(sageMakerEndPoint), any(HttpEntity.class), eq(Map.class))).thenReturn(apiResponse);
       when(restTemplate.exchange(anyString(), any(), any(), eq(MusicGenerationResponse.class)))
               .thenReturn(ResponseEntity.ok(failedResponse));

       CompletableFuture<String> futureResult = musicGenerationService.generateAndUploadMusic(request);
       assertThrows(CompletionException.class, futureResult::join);
   }
}