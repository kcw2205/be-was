package webserver.http.data;

import webserver.http.enums.HttpRequestMethod;

import java.util.Map;

public class HttpRequest {
    private final HttpRequestMethod requestMethod;
    private final String httpVersion;
    private final String requestURI;
    private final Map<String, String> headers;
    private final Map<String, String> queryParameters;
    private final HttpBody body;

    public HttpRequest(
        HttpRequestMethod requestMethod,
        String requestURI,
        String httpVersion,
        Map<String, String> headers,
        Map<String, String> queryParameters,
        HttpBody body
    ) {
        this.requestMethod = requestMethod;
        this.httpVersion = httpVersion;
        this.requestURI = requestURI;
        this.headers = headers;
        this.queryParameters = queryParameters;
        this.body = body;
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

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }
}
