package webserver.http;

import webserver.http.enums.HttpStatusCode;

// TODO: Checked vs Unchecked, 어느쪽으로 하면 좋을지 고민하기
public class HttpException extends Exception {
    private final HttpStatusCode statusCode;

    public HttpException(HttpStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
