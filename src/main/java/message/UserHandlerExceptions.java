package message;

import webserver.http.HttpException;
import webserver.http.enums.HttpStatusCode;

public enum UserHandlerExceptions {

    USER_NOT_FOUND(HttpStatusCode.NOT_FOUND, "해당 아이디를 가진 유저를 찾을 수 없습니다."),
    BAD_REQUEST_FORMAT(HttpStatusCode.BAD_REQUEST, "유효하지 않은 요청 형식입니다"),
    SAME_ID_EXISTS(HttpStatusCode.FORBIDDEN, "존재하는 아이디입니다. 다른 아이디를 입력하세요."),
    WRONG_PASSWORD(HttpStatusCode.FORBIDDEN, "비밀번호가 틀렸습니다"),
    NOT_LOGGED_IN(HttpStatusCode.FORBIDDEN, "로그인 되지 않았습니다"),
    ;

    private final String message;
    private final HttpStatusCode httpStatusCode;

    UserHandlerExceptions(HttpStatusCode httpStatusCode, String message) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    public String getMessage() {
        return this.message;
    }

    public HttpException toException() {
        return new HttpException(this.httpStatusCode, this.message);
    }
}
