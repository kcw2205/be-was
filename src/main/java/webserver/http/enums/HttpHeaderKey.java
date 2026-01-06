package webserver.http.enums;

public enum HttpHeaderKey {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    CONNECTION("Connection"),
    KEEP_ALIVE("Keep-Alive"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location"),
    COOKIE("Cookie");

    private final String name;

    HttpHeaderKey(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
