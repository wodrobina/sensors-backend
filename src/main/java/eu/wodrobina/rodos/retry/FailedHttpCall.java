package eu.wodrobina.rodos.retry;

import java.time.LocalDateTime;
import java.util.UUID;

class FailedHttpCall {

    private final UUID id;
    private final String url;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastAttemptAt;
    private final int attempts;

    FailedHttpCall(
            UUID id,
            String url,
            LocalDateTime createdAt,
            LocalDateTime lastAttemptAt,
            int attempts
    ) {
        this.id = id;
        this.url = url;
        this.createdAt = createdAt;
        this.lastAttemptAt = lastAttemptAt;
        this.attempts = attempts;
    }

    UUID getId() {
        return id;
    }

    String getUrl() {
        return url;
    }

    LocalDateTime getCreatedAt() {
        return createdAt;
    }

    LocalDateTime getLastAttemptAt() {
        return lastAttemptAt;
    }

    int getAttempts() {
        return attempts;
    }
}