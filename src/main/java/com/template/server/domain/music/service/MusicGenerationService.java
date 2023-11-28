package com.template.server.domain.music.service;

import com.template.server.domain.music.dto.request.MusicGenerationInput;
import com.template.server.domain.music.dto.request.MusicGenerationRequest;
import com.template.server.domain.music.dto.request.MusicPromptRequest;
import com.template.server.domain.music.dto.response.MusicGenerationResponse;
import com.template.server.global.util.aws.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class MusicGenerationService {

    private final RestTemplate restTemplate;
    private final S3UploadService s3UploadService;


    @Value(("${sagemaker.api-key}"))
    private String sageMakerKey;
    @Value(("${sagemaker.end-point}"))
    private String sageMakerEndPoint;


    public String generateAndUploadMusic(MusicPromptRequest musicPromptRequest) throws InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Token " + sageMakerKey);

        MusicGenerationInput musicGenerationInput = MusicGenerationInput.builder()
                .topK(250)
                .topP(0)
                .prompt(musicPromptRequest.getPrompt())
                .duration(5)
                .temperature(1)
                .continuation(false)
                .modelVersion("stereo-large")
                .outputFormat("mp3")
                .continuationStart(0)
                .multiBandDiffusion(false)
                .normalizationStrategy("peak")
                .classifierFreeGuidance(3)
                .build();

        MusicGenerationRequest musicGenerationRequest = MusicGenerationRequest.builder()
                .version("7be0f12c54a8d033a0fbd14418c9af98962da9a86f5ff7811f9b3423a1f0b7d7")
                .input(musicGenerationInput)
                .build();

        HttpEntity<MusicGenerationRequest> entity = new HttpEntity<>(musicGenerationRequest, headers);
        Map<String, Object> response = restTemplate.postForObject(sageMakerEndPoint, entity, Map.class);

        if (response == null || !response.containsKey("id")) {
            throw new RuntimeException("Failed to get response from Replicate API");
        }

        String predictionId = (String) response.get("id");
        String getResultUrl = sageMakerEndPoint + "/" + predictionId;

        while (true) {
            HttpHeaders getResultHeaders = new HttpHeaders();
            getResultHeaders.set("Authorization", "Token " + sageMakerKey);
            HttpEntity<String> getResultEntity = new HttpEntity<>(getResultHeaders);

            ResponseEntity<MusicGenerationResponse> resultResponseEntity = restTemplate.exchange(
                    getResultUrl,
                    HttpMethod.GET,
                    getResultEntity,
                    MusicGenerationResponse.class);

            MusicGenerationResponse musicGenerationResponse = resultResponseEntity.getBody();

            if (musicGenerationResponse != null) {
                String status = musicGenerationResponse.getStatus();

                if ("succeeded".equals(status)) {
                    String musicUrl = musicGenerationResponse.getOutput();
                    byte[] musicData = restTemplate.execute(
                            musicUrl,
                            HttpMethod.GET,
                            null,
                            clientHttpResponse -> StreamUtils.copyToByteArray(clientHttpResponse.getBody())
                    );
                    return s3UploadService.uploadMp3AndReturnCloudFrontUrl(musicData);
                } else if ("failed".equals(status) || "canceled".equals(status)) {
                    throw new RuntimeException("Music generation failed or was canceled");
                }
                Thread.sleep(5000); // TODO : 효율개선이 필요해보임
            }
        }
    }
}
