package com.template.server.domain.health;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/health-redis")
    public ResponseEntity<String> healthCheckRedis() {
        try (RedisConnection connection = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection()) {
            String response = connection.ping();

            if ("PONG".equalsIgnoreCase(response)) {
                return ResponseEntity.ok("OK");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected response from Redis");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Redis connection failed: " + e.getMessage());
        }
    }
}
