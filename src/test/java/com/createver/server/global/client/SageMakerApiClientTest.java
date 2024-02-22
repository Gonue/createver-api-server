package com.createver.server.global.client;

import com.createver.server.domain.image.dto.request.ImageAvatarRequest;
import com.createver.server.global.config.SageMakerConfig;
import com.createver.server.global.error.exception.BusinessLogicException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("SageMaker Api Client 테스트")
@ExtendWith(MockitoExtension.class)
class SageMakerApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SageMakerApiClient sageMakerApiClient;
    private String sageMakerEndPoint;


    @Test
    @DisplayName("SageMaker API 호출 성공")
    void callSageMakerApi_Success() {
        // Given
        ImageAvatarRequest request = new ImageAvatarRequest();
        Map<String, Object> responseBody = Map.of(SageMakerConfig.RESPONSE_KEY_ID, "uniqueId");
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(eq(sageMakerEndPoint), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        // When
        String result = sageMakerApiClient.callSageMakerApi(request);

        // Then
        assertEquals("uniqueId", result);
    }

    @Test
    @DisplayName("SageMaker API 호출 실패 - 응답 본문 없음")
    void callSageMakerApi_NoResponseBody() {
        // Given
        ImageAvatarRequest request = new ImageAvatarRequest();

        when(restTemplate.postForEntity(eq(sageMakerEndPoint), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        // When & Then
        assertThrows(BusinessLogicException.class, () -> sageMakerApiClient.callSageMakerApi(request));
    }

    @Test
    @DisplayName("SageMaker API 호출 실패 - HttpClientErrorException")
    void callSageMakerApi_HttpClientErrorException() {
        // Given
        ImageAvatarRequest request = new ImageAvatarRequest();

        when(restTemplate.postForEntity(eq(sageMakerEndPoint), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(HttpClientErrorException.class);

        // When & Then
        assertThrows(BusinessLogicException.class, () -> sageMakerApiClient.callSageMakerApi(request));
    }
}