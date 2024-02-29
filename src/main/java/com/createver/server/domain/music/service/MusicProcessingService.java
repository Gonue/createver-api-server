package com.createver.server.domain.music.service;

import com.createver.server.domain.music.dto.response.MusicWebhookResponse;
import com.createver.server.domain.music.entity.Music;
import com.createver.server.domain.music.repository.music.MusicRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import com.createver.server.global.sse.SseService;
import com.createver.server.global.util.aws.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MusicProcessingService {

    private final MusicRepository musicRepository;
    private final S3UploadService s3UploadService;
    private final SseService sseService;
    private final RestTemplate  restTemplate;

    public void processWebhookResponse(MusicWebhookResponse musicWebhookResponse) {
        String predictionId = musicWebhookResponse.getId();
        String musicUrl = musicWebhookResponse.getMusicUrl();

        musicRepository.findByPredictionId(predictionId).ifPresent(music -> {
            if (musicUrl != null && !musicUrl.isEmpty()) {
                try {
                    byte[] musicData = restTemplate.execute(
                            musicUrl,
                            HttpMethod.GET,
                            null,
                            clientHttpResponse -> StreamUtils.copyToByteArray(clientHttpResponse.getBody())
                    );
                    String s3Url = s3UploadService.uploadWavAndReturnCloudFrontUrl(musicData);
                    music.updateResultMusicAndStatus(s3Url, "completed");
                    musicRepository.save(music);

                    sendSseEvent(predictionId, s3Url);
                } catch (BusinessLogicException e) {
                    log.error("Error downloading music from URL: " + musicUrl, e);
                    throw new BusinessLogicException(ExceptionCode.SAGEMAKER_NO_RESPONSE);
                }
            }
        });
    }

    private void sendSseEvent(String predictionId, String musicUrl) {
        List<SseEmitter> emitters = sseService.getEmitters(predictionId);
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("musicProcessed").data(musicUrl));
                emitter.complete();
            } catch (IOException e){
                emitter.completeWithError(e);
            }
        }
    }
}
