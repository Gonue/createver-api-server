package com.createver.server.domain.music.service;

import com.createver.server.domain.music.dto.request.MusicGenerationInput;
import com.createver.server.domain.music.dto.request.MusicGenerationRequest;
import com.createver.server.domain.music.dto.request.MusicPromptRequest;
import com.createver.server.domain.music.dto.response.MusicGenerationResponse;
import com.createver.server.global.util.aws.service.S3UploadService;
import com.createver.server.global.util.translate.LanguageDiscriminationUtils;
import com.createver.server.global.util.translate.Translate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;


@Service
@RequiredArgsConstructor
public class MusicGenerationService {

    private final RestTemplate restTemplate;
    private final S3UploadService s3UploadService;
    private final Translate translate;

    @Value("${sagemaker.api-key}")
    private String sageMakerKey;
    @Value("${sagemaker.end-point}")
    private String sageMakerEndPoint;

    @Async
    public CompletableFuture<String> generateAndUploadMusic(MusicPromptRequest musicPromptRequest) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Token " + sageMakerKey);

                String translatedPrompt = musicPromptRequest.getPrompt();
                if (LanguageDiscriminationUtils.isKorean(translatedPrompt)) {
                    translatedPrompt = translate.translate(translatedPrompt, "ko", "en");
                }

                MusicGenerationInput musicGenerationInput = MusicGenerationInput.builder()
                        .topK(250)
                        .topP(0)
                        .prompt(translatedPrompt)
                        .duration(5)
                        .temperature(1)
                        .continuation(false)
                        .modelVersion("stereo-large")
                        .outputFormat("wav")
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
                return pollForResult(getResultUrl).join();
            } catch (Exception e) {
                throw new RuntimeException("Error during music generation", e);
            }
        });
    }

    private CompletableFuture<String> pollForResult(String getResultUrl) {
        return CompletableFuture.supplyAsync(() -> {
            HttpHeaders getResultHeaders = new HttpHeaders();
            getResultHeaders.set("Authorization", "Token " + sageMakerKey);
            HttpEntity<String> getResultEntity = new HttpEntity<>(getResultHeaders);

            ResponseEntity<MusicGenerationResponse> resultResponseEntity = restTemplate.exchange(
                    getResultUrl,
                    HttpMethod.GET,
                    getResultEntity,
                    MusicGenerationResponse.class);

            return resultResponseEntity.getBody();
        }).thenCompose(musicGenerationResponse -> {
            if (musicGenerationResponse == null) {
                throw new RuntimeException("No response from music generation service");
            }

            String status = musicGenerationResponse.getStatus();
            switch (status) {
                case "succeeded":
                    String musicUrl = musicGenerationResponse.getOutput();
                    byte[] musicData = restTemplate.execute(
                            musicUrl,
                            HttpMethod.GET,
                            null,
                            clientHttpResponse -> StreamUtils.copyToByteArray(clientHttpResponse.getBody())
                    );
                    return CompletableFuture.completedFuture(s3UploadService.uploadWavAndReturnCloudFrontUrl(musicData));
                case "failed", "canceled":
                    throw new RuntimeException("Music generation failed or was canceled");
                default:
                    return CompletableFuture.supplyAsync(() -> {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Interrupted during polling", e);
                        }
                        return pollForResult(getResultUrl);
                    }).thenCompose(Function.identity());
            }
        });
    }
}
