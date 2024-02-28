package com.createver.server.global.sse;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {

    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    // 새 SSE 연결 추가
    public void addEmitter(String id, SseEmitter emitter) {
        emitters.computeIfAbsent(id, k -> new CopyOnWriteArrayList<>()).add(emitter);
    }

    // SSE 연결 제거
    public void removeEmitter(String id, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.getOrDefault(id, new CopyOnWriteArrayList<>());
        userEmitters.remove(emitter);
        if (userEmitters.isEmpty()) {
            emitters.remove(id);
        }
    }

    // ID에 해당하는 SSE 연결 목록 가져오기
    public List<SseEmitter> getEmitters(String id) {
        return emitters.getOrDefault(id, new CopyOnWriteArrayList<>());
    }
}