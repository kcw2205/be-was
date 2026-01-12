package webserver.session;

import java.time.LocalDateTime;

public record Session(Object payload, LocalDateTime expiration) {

    public boolean isExpired() {
        return expiration.isBefore(LocalDateTime.now());
    }
}