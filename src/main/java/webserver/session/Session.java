package webserver.session;

import java.time.LocalDateTime;

public class Session {
    private Object payload;
    private LocalDateTime expiration;

    public Session(Object payload, LocalDateTime expiration) {
        this.payload = payload;
        this.expiration = expiration;
    }

    public void updatePayload(Object payload) {
        this.payload = payload;
    }

    public boolean isExpired() {
        return expiration.isBefore(LocalDateTime.now());
    }

    public Object getPayload() {
        return payload;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }
}