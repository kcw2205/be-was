package webserver.http.data;

import webserver.session.SessionManager;

public class Cookie {
    private long maxAge = SessionManager.SESSION_EXPIRATION.getSeconds();
    private final String name;
    private final String value;
    private String path;
    private boolean isHttpOnly = false;

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // TODO: 세터 열기?
    public void setPath(String path) {
        this.path = path;
    }

    public void setMaxAge(long maxAge) {
        if (maxAge < 0) {
            throw new IllegalArgumentException("maxAge cannot be negative");
        }
        this.maxAge = maxAge;
    }

    public void setHttpOnly(boolean isHttpOnly) {
        this.isHttpOnly = isHttpOnly;
    }

    public String serialize() {
        String nameValue = name + "=" + value;
        if (maxAge != -1) {
            nameValue += "; Max-Age=" + maxAge;
        }
        if (path != null) {
            nameValue += "; path=" + path;
        }
        if (isHttpOnly) {
            nameValue += "; HttpOnly";
        }

        return nameValue;
    }
}
