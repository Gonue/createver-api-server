package com.createver.server.global.client;

import com.createver.server.domain.image.dto.request.ImageGenerationRequest;
import com.createver.server.domain.image.dto.response.ImageGenerationResponse;
import com.createver.server.global.config.OpenAiConfig;
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

@Component
@RequiredArgsConstructor
@Slf4j
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
                log.error("OpenAi API did not return 'body'");
                throw new BusinessLogicException(ExceptionCode.OPENAI_NO_RESPONSE, "OpenAI API No Response");
            }

            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error calling OpenAI API: {}", e.getMessage());
            throw new BusinessLogicException(ExceptionCode.OPENAI_API_ERROR);
        }
    }
}
