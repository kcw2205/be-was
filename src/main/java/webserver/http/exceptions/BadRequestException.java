package webserver.http.exceptions;

import webserver.http.enums.HttpStatusCode;

public class BadRequestException extends HttpException {

    public BadRequestException(String message) {
        super(HttpStatusCode.BAD_REQUEST, message);
    }

    public BadRequestException() {
        super(HttpStatusCode.BAD_REQUEST, HttpStatusCode.BAD_REQUEST.toString());
    }
}
