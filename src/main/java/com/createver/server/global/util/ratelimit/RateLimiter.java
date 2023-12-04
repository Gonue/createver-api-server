package com.createver.server.global.util.ratelimit;

public class RateLimiter {
    private final int maxTokens;
    private final long refillInterval;
    private int tokens;
    private long nextRefillTime;

    public RateLimiter(int maxTokens, long refillInterval) {
        this.maxTokens = maxTokens;
        this.refillInterval = refillInterval;
        this.tokens = maxTokens;
        this.nextRefillTime = System.nanoTime() + refillInterval;
    }

    public synchronized boolean allowRequest() {
        long now = System.nanoTime();
        if (now > nextRefillTime) {
            tokens = maxTokens;
            nextRefillTime = now + refillInterval;
        }
        if (tokens > 0) {
            tokens--;
            return true;
        } else {
            return false;
        }
    }
}