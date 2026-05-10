package pe.edu.upc.qhurinet.securities;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {
    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_SECONDS = 60;

    private final Map<String, Deque<Instant>> attempts = new ConcurrentHashMap<>();

    public boolean allow(String key) {
        Instant now = Instant.now();
        Deque<Instant> queue = attempts.computeIfAbsent(key, ignored -> new ArrayDeque<>());

        synchronized (queue) {
            while (!queue.isEmpty() && queue.peekFirst().plusSeconds(WINDOW_SECONDS).isBefore(now)) {
                queue.removeFirst();
            }
            if (queue.size() >= MAX_ATTEMPTS) {
                return false;
            }
            queue.addLast(now);
            return true;
        }
    }
}
