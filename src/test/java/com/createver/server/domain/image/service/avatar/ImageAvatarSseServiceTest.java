package com.createver.server.domain.image.service;

import com.createver.server.domain.image.service.avatar.ImageAvatarSseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Image Avatar Sse Servie 테스트")
@ExtendWith(MockitoExtension.class)

class ImageAvatarSseServiceTest {

    @DisplayName("새 SSE 연결 추가 테스트")
    @Test
    void addEmitter_AddsNewEmitterSuccessfully() {
        ImageAvatarSseService service = new ImageAvatarSseService();
        SseEmitter emitter = new SseEmitter();
        String predictionId = "testId";

        service.addEmitter(predictionId, emitter);

        List<SseEmitter> emitters = service.getEmitters(predictionId);
        assertTrue(emitters.contains(emitter));
    }

    @DisplayName("SSE 연결 제거 테스트")
    @Test
    void removeEmitter_RemovesEmitterSuccessfully() {
        ImageAvatarSseService service = new ImageAvatarSseService();
        SseEmitter emitter1 = new SseEmitter();
        SseEmitter emitter2 = new SseEmitter();
        String predictionId = "testId";

        service.addEmitter(predictionId, emitter1);
        service.addEmitter(predictionId, emitter2);
        service.removeEmitter(predictionId, emitter1);

        List<SseEmitter> emitters = service.getEmitters(predictionId);
        assertFalse(emitters.contains(emitter1));
        assertTrue(emitters.contains(emitter2));
    }

    @DisplayName("ID에 해당하는 SSE 연결 목록 가져오기 테스트")
    @Test
    void getEmitters_ReturnsCorrectEmittersForId() {
        ImageAvatarSseService service = new ImageAvatarSseService();
        SseEmitter emitter1 = new SseEmitter();
        SseEmitter emitter2 = new SseEmitter();
        String predictionId1 = "testId1";
        String predictionId2 = "testId2";

        service.addEmitter(predictionId1, emitter1);
        service.addEmitter(predictionId2, emitter2);

        List<SseEmitter> emittersForId1 = service.getEmitters(predictionId1);
        List<SseEmitter> emittersForId2 = service.getEmitters(predictionId2);

        assertTrue(emittersForId1.contains(emitter1));
        assertFalse(emittersForId1.contains(emitter2));

        assertTrue(emittersForId2.contains(emitter2));
        assertFalse(emittersForId2.contains(emitter1));
    }

}