package com.createver.server.global.client;

import com.createver.server.domain.image.dto.request.ImageGenerationRequest;
import com.createver.server.domain.image.dto.response.ImageGenerationResponse;
import com.createver.server.global.config.OpenAiConfig;
import com.createver.server.global.error.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("OpenAi Api Client 테스트")
@ExtendWith(MockitoExtension.class)
class OpenAiApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OpenAiApiClient openAiApiClient;

    @Test
    @DisplayName("OpenAI API 호출 성공 시")
    void generateImageSuccess() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest();
        ImageGenerationResponse expectedResponse = new ImageGenerationResponse();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(OpenAiConfig.MEDIA_TYPE));
        headers.add(OpenAiConfig.AUTHORIZATION, OpenAiConfig.BEARER + "your_api_key_here");

        ResponseEntity<ImageGenerationResponse> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(eq(OpenAiConfig.IMAGE_URL), any(HttpEntity.class), eq(ImageGenerationResponse.class)))
                .thenReturn(responseEntity);

        // When
        ImageGenerationResponse actualResponse = openAiApiClient.generateImage(request);

        // Then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("HttpClientErrorException 발생 시")
    void generateImageFailureDueToHttpClientErrorException() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest();
        when(restTemplate.postForEntity(eq(OpenAiConfig.IMAGE_URL), any(HttpEntity.class), eq(ImageGenerationResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        // When & Then
        assertThrows(BusinessLogicException.class, () -> openAiApiClient.generateImage(request));
    }

    @Test
    @DisplayName("HttpClientErrorException 처리")
    void generateImageShouldHandleHttpClientErrorException() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest();
        when(restTemplate.postForEntity(eq(OpenAiConfig.IMAGE_URL), any(HttpEntity.class), eq(ImageGenerationResponse.class)))
                .thenThrow(HttpClientErrorException.class);

        // When & Then
        assertThrows(BusinessLogicException.class, () -> openAiApiClient.generateImage(request));
    }
    @Test
    @DisplayName("응답 본문이 null인 경우 처리")
    void generateImageNoResponseBody() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest();
        ResponseEntity<ImageGenerationResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.postForEntity(eq(OpenAiConfig.IMAGE_URL), any(HttpEntity.class), eq(ImageGenerationResponse.class)))
                .thenReturn(responseEntity);

        // When & Then
        assertThrows(BusinessLogicException.class, () -> openAiApiClient.generateImage(request));
    }

}