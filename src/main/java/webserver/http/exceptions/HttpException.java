package webserver.http.exceptions;

import webserver.http.enums.HttpStatusCode;

class HttpException extends Exception {
    private final HttpStatusCode statusCode;

    public HttpException(HttpStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
