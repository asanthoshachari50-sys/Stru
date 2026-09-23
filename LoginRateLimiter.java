package com.example.secureapp.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {
    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_SECONDS = 60;

    private final Map<String, AttemptWindow> attempts = new ConcurrentHashMap<>();

    public boolean allowed(String key) {
        Instant now = Instant.now();
        AttemptWindow window = attempts.compute(key, (k, old) -> {
            if (old == null || now.getEpochSecond() - old.startedAt >= WINDOW_SECONDS) {
                return new AttemptWindow(now.getEpochSecond(), 1);
            }
            return new AttemptWindow(old.startedAt, old.count + 1);
        });
        return window.count <= MAX_ATTEMPTS;
    }

    private record AttemptWindow(long startedAt, int count) {}
}
