package webserver.http.enums;

public enum HttpStatusCode {
    METHOD_NOT_FOUND(405), OK(200), UNAUTHORIZED(401), NOT_MODIFIED(304), NOT_FOUND(404), NO_CONTENT(204), INTERNAL_SERVER_ERROR(500);

    private final int code;

    HttpStatusCode(int i) {
        this.code = i;
    }

    public int getCode() {
        return this.code;
    }

    public String getStatusName() {
        return this.name().replace("_", " ");
    }
}
