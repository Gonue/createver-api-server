package com.createver.server.global.client;

import com.createver.server.domain.image.dto.request.ImageAvatarRequest;
import com.createver.server.global.config.SageMakerConfig;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SageMakerApiClient {

    private final RestTemplate restTemplate;

    @Value("${sagemaker.api-key}")
    private String sageMakerKey;
    @Value("${sagemaker.end-point}")
    private String sageMakerEndPoint;

    public String callSageMakerApi(ImageAvatarRequest request) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(SageMakerConfig.MEDIA_TYPE));
            httpHeaders.add(SageMakerConfig.AUTHORIZATION, SageMakerConfig.TOKEN + sageMakerKey);
            HttpEntity<ImageAvatarRequest> entity = new HttpEntity<>(request, httpHeaders);
            ResponseEntity<Map> response = restTemplate.postForEntity(sageMakerEndPoint, entity, Map.class);

            Map body = response.getBody();
            if (body == null || !body.containsKey(SageMakerConfig.RESPONSE_KEY_ID)) {
                log.error("SageMaker API did not return an 'id'");
                throw new BusinessLogicException(ExceptionCode.SAGEMAKER_NO_RESPONSE, "Failed to get response from SageMaker API");
            }
            return (String) body.get(SageMakerConfig.RESPONSE_KEY_ID);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error calling SageMaker API: {}", e.getMessage());
            throw new BusinessLogicException(ExceptionCode.SAGEMAKER_API_ERROR);
        }
    }
}
