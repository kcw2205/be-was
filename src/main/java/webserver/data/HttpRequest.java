package webserver.data;

import webserver.data.enums.HttpRequestMethod;

import java.util.Map;

public class HttpRequest {
    private final HttpRequestMethod requestMethod;
    private final String httpVersion;
    private final String requestURI;
    private final Map<String, String> headers;
    private final HttpBody body;

    public HttpRequest(
            HttpRequestMethod requestMethod,
            String requestURI,
            String httpVersion,
            Map<String, String> headers,
            HttpBody body
    ) {
        this.requestMethod = requestMethod;
        this.httpVersion = httpVersion;
        this.requestURI = requestURI;
        this.body = body;
        this.headers = headers;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpBody getBody() {
        return body;
    }

    public HttpRequestMethod getRequestMethod() {
        return requestMethod;
    }
}
