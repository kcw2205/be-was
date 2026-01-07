package webserver.handling;


import webserver.http.data.HttpResponse;
import webserver.http.enums.HttpHeaderKey;
import webserver.http.enums.HttpStatusCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseEntity<T> {
    private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1"; // enum 으로 아직은 설정할 필요 없다고 판단
    private final byte[] data;
    private final HttpStatusCode httpStatusCode;
    private final Map<String, String> headers = new HashMap<>();
    private final List<String> cookies = new ArrayList<>();

    private ResponseEntity(T data, HttpStatusCode status, String contentType) {
        this.httpStatusCode = status;
        if (contentType != null) this.headers.put(HttpHeaderKey.CONTENT_TYPE.toString(), contentType);

        // TODO: ObjectMapper 사용하기..!
        if (data instanceof String) {
            this.data = ((String) data).getBytes(); // 텍스트는 인코딩해서 바이트로
        } else if (data instanceof byte[]) {
            this.data = (byte[]) data;
        } else if (data == null) {
            this.data = new byte[0];
        } else {
            this.data = data.toString().getBytes();
        }

        if (contentType != null)
            this.headers.put(HttpHeaderKey.CONTENT_LENGTH.toString(), String.valueOf(this.data.length));
    }

    public static ResponseEntity<Void> empty(HttpStatusCode status) {
        return new ResponseEntity<>(null, status, null);
    }

    public static <T> ResponseEntity<T> builder(T data, HttpStatusCode status, String contentType) {
        return new ResponseEntity<T>(data, status, contentType);
    }

    public static <T> ResponseEntity<T> ok(T data, String contentType) {
        return new ResponseEntity<>(data, HttpStatusCode.OK, contentType);
    }

    public static ResponseEntity<?> notFound() {
        return new ResponseEntity<>(HttpStatusCode.NOT_FOUND.getStatusName(), HttpStatusCode.NOT_FOUND, "text/plain");
    }

    public static ResponseEntity<Object> badRequest() {
        return new ResponseEntity<>(HttpStatusCode.BAD_REQUEST.getStatusName(), HttpStatusCode.BAD_REQUEST, "text/plain");
    }

    public static ResponseEntity<Object> internalServerError() {
        return new ResponseEntity<>(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusName(), HttpStatusCode.BAD_REQUEST, "text/plain");
    }

    public ResponseEntity<T> addHeader(HttpHeaderKey key, String value) {
        this.headers.put(key.toString(), value);
        return this;
    }

    // TODO: 안티패턴 개선하기
    public ResponseEntity<T> addCookie(String key, String value, String path, boolean isHttpOnly) {
        this.cookies.add(key + "=" + value + "; Path=" + path + "; HttpOnly=" + isHttpOnly);
        return this;
    }

    public ResponseEntity<T> addCookie(String key, String value, String path) {
        this.cookies.add(key + "=" + value + "; Path=" + path);
        return this;
    }

    public ResponseEntity<T> addCookie(String key, String value) {
        this.cookies.add(key + "=" + value);
        return this;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public HttpResponse toHttpResponse() {
        if (!this.cookies.isEmpty()) addCookiesToHeader();

        return new HttpResponse(httpStatusCode, DEFAULT_HTTP_VERSION, headers, data);
    }

    private void addCookiesToHeader() {
        this.cookies.forEach(cookie -> {
            this.headers.put(HttpHeaderKey.SET_COOKIE.toString(), cookie);
        });
    }
}
