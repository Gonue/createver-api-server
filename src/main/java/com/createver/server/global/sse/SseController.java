package com.createver.server.global.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stream")
public class SseController {

    private final SseService sseService;

    @GetMapping(value = "/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable String id) {
        SseEmitter emitter = new SseEmitter(120000L);
        sseService.addEmitter(id, emitter);

        emitter.onCompletion(() -> sseService.removeEmitter(id, emitter));
        emitter.onTimeout(() -> sseService.removeEmitter(id, emitter));
        emitter.onError(e -> sseService.removeEmitter(id, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connectionTest")
                    .data("Test Message - Connection Established"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }
}
