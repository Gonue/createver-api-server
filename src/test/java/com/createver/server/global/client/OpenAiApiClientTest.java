package com.createver.server.domain.image.service.gallery;

import com.createver.server.domain.image.dto.request.ImageGenerationRequest;
import com.createver.server.domain.image.dto.response.ImageGenerationResponse;
import com.createver.server.global.client.OpenAiApiClient;
import com.createver.server.global.config.OpenAiConfig;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
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

@DisplayName("OpenAi Service 테스트")
@ExtendWith(MockitoExtension.class)
class OpenAiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OpenAiApiClient openAiApiClient;

    @Test
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
    void generateImageFailure() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest();
        when(restTemplate.postForEntity(eq(OpenAiConfig.IMAGE_URL), any(HttpEntity.class), eq(ImageGenerationResponse.class)))
                .thenThrow(new BusinessLogicException(ExceptionCode.OPENAI_API_ERROR, "OpenAI API 호출 실패"));

        // When & Then
        assertThrows(BusinessLogicException.class, () -> openAiApiClient.generateImage(request));
    }

    @Test
    void generateImageShouldHandleHttpClientErrorException() {
        // Given
        ImageGenerationRequest request = new ImageGenerationRequest();
        when(restTemplate.postForEntity(eq(OpenAiConfig.IMAGE_URL), any(HttpEntity.class), eq(ImageGenerationResponse.class)))
                .thenThrow(HttpClientErrorException.class);

        // When & Then
        assertThrows(BusinessLogicException.class, () -> openAiApiClient.generateImage(request));
    }


}