package com.example.sgtpro.SGTPRO.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimitingService {

    private final ConcurrentHashMap<String, Attempt> attempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MS = TimeUnit.MINUTES.toMillis(15);

    public boolean isBlocked(String ip) {
        Attempt att = attempts.get(ip);
        return att != null && !att.isExpired(WINDOW_MS) && att.count > MAX_ATTEMPTS;
    }

    public void registerFailed(String ip) {
        attempts.compute(ip, (key, val) -> {
            if (val == null || val.isExpired(WINDOW_MS)) {
                return Attempt.initial();
            }
            return val.increment();
        });
    }

    public void registerSuccess(String ip) {
        attempts.remove(ip);
    }

    private static final class Attempt {
        private final long windowStart;
        private final int count;

        private Attempt(long windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }

        static Attempt initial() {
            return new Attempt(System.currentTimeMillis(), 1);
        }

        boolean isExpired(long windowMs) {
            return System.currentTimeMillis() - windowStart > windowMs;
        }

        Attempt increment() {
            return new Attempt(this.windowStart, this.count + 1);
        }
    }
}
