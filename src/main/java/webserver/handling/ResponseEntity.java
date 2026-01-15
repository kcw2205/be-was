package webserver.handling;


import webserver.http.HttpResponseMapper;
import webserver.http.data.Cookie;
import webserver.http.data.HttpResponse;
import webserver.http.enums.HttpContentType;
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
    private final List<Cookie> cookies = new ArrayList<>();

    private ResponseEntity(T data, HttpStatusCode status, HttpContentType contentType) {
        this.httpStatusCode = status;
        if (contentType != null) this.headers.put(HttpHeaderKey.CONTENT_TYPE.toString(), contentType.toString());

        this.data = HttpResponseMapper.mapToByteArray(data);

        if (contentType != null)
            this.headers.put(HttpHeaderKey.CONTENT_LENGTH.toString(), String.valueOf(this.data.length));
    }

    public static ResponseEntity<Void> simple(HttpStatusCode status) {
        return new ResponseEntity<>(null, status, HttpContentType.TEXT_PLAIN);
    }

    public static <T> ResponseEntity<T> create(T data, HttpStatusCode status, HttpContentType contentType) {
        return new ResponseEntity<T>(data, status, contentType);
    }

    public static <T> ResponseEntity<T> ok(T data, HttpContentType contentType) {
        return new ResponseEntity<>(data, HttpStatusCode.OK, contentType);
    }

    public static ResponseEntity<Void> ok() {
        return new ResponseEntity<>(null, HttpStatusCode.OK, HttpContentType.OCTET_STREAM);
    }

    public ResponseEntity<T> addHeader(HttpHeaderKey key, String value) {
        this.headers.put(key.toString(), value);
        return this;
    }

    public ResponseEntity<T> addCookie(Cookie cookie) {
        this.cookies.add(cookie);
        return this;
    }

    public ResponseEntity<T> addCookiePrimitive(String key, String value) {
        this.cookies.add(new Cookie(key, value));
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
            this.headers.put(HttpHeaderKey.SET_COOKIE.toString(), cookie.serialize());
        });
    }
}
