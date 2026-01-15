package exception;

import webserver.http.HttpException;
import webserver.http.enums.HttpStatusCode;

public enum ServiceErrorCode {

    USER_NOT_FOUND(HttpStatusCode.NOT_FOUND, "해당 아이디를 가진 유저를 찾을 수 없습니다."),
    BAD_REQUEST_FORMAT(HttpStatusCode.BAD_REQUEST, "유효하지 않은 요청 형식입니다"),
    SAME_ID_EXISTS(HttpStatusCode.FORBIDDEN, "존재하는 아이디입니다. 다른 아이디를 입력하세요."),
    WRONG_PASSWORD(HttpStatusCode.FORBIDDEN, "비밀번호가 틀렸습니다"),
    NOT_LOGGED_IN(HttpStatusCode.FORBIDDEN, "로그인 되지 않았습니다"),
    SAME_NAME_EXISTS(HttpStatusCode.FORBIDDEN, "존재하는 닉네임입니다. 다른 닉네임을 입력하세요."),
    IMAGE_UPLOAD_ERROR(HttpStatusCode.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
    RESOURCE_NOT_FOUND(HttpStatusCode.NOT_FOUND, "리소스를 찾을 수 없습니다."),
    DATA_VALIDATION_ERROR(HttpStatusCode.INTERNAL_SERVER_ERROR, "데이터를 불러오는데 실패했습니다.");

    private final String message;
    private final HttpStatusCode httpStatusCode;

    ServiceErrorCode(HttpStatusCode httpStatusCode, String message) {
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
