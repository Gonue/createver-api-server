package com.createver.server.domain.image.service.gallery;

import com.createver.server.domain.image.dto.request.ImageGenerationRequest;
import com.createver.server.domain.image.dto.response.ImageGenerationResponse;
import com.createver.server.global.config.OpenAiConfig;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OpenAiApiClient {

    private final RestTemplate restTemplate;

    @Value("${openai.api-key}")
    private String apiKey;

    public ImageGenerationResponse generateImage(ImageGenerationRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(OpenAiConfig.MEDIA_TYPE));
            headers.add(OpenAiConfig.AUTHORIZATION, OpenAiConfig.BEARER + apiKey);

            HttpEntity<ImageGenerationRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<ImageGenerationResponse> response = restTemplate.postForEntity(
                    OpenAiConfig.IMAGE_URL,
                    entity,
                    ImageGenerationResponse.class);

            if (response.getBody() == null) {
                throw new BusinessLogicException(ExceptionCode.GENERAL_ERROR, "OpenAI API No Response");
            }

            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new BusinessLogicException(ExceptionCode.OPENAI_API_ERROR, "OpenAI API 호출 실패");
        }
    }
}
