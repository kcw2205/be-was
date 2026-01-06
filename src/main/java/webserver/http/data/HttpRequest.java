package webserver.http.data;

import webserver.http.enums.HttpHeaderKey;
import webserver.http.enums.HttpRequestMethod;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final HttpRequestMethod requestMethod;
    private final String httpVersion;
    private final String requestURI;
    private final Map<String, String> headers;
    private final Map<String, String> cookies;
    private final Map<String, String> queryParameters;
    private final HttpRequestBody body;

    public HttpRequest(
        HttpRequestMethod requestMethod,
        String requestURI,
        String httpVersion,
        Map<String, String> headers,
        Map<String, String> queryParameters,
        HttpRequestBody body
    ) {
        this.requestMethod = requestMethod;
        this.httpVersion = httpVersion;
        this.requestURI = requestURI;
        this.headers = headers;
        this.cookies = parseCookies(headers.getOrDefault(HttpHeaderKey.COOKIE, ""));
        this.queryParameters = queryParameters;
        this.body = body;
    }

    private Map<String, String> parseCookies(String str) {
        Map<String, String> map = new HashMap<>();
        for (String t : str.split(";")) {
            String[] cookiePair = t.trim().split("=", 2);
            if (cookiePair.length < 2) continue;
            map.put(cookiePair[0].trim(), cookiePair[1].trim());
        }

        return map;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String searchHeaderAttribute(String attributeName) {
        attributeName = attributeName.toLowerCase();
        return this.headers.getOrDefault(attributeName, null);
    }

    public HttpRequestBody getBody() {
        return body;
    }

    public HttpRequestMethod getRequestMethod() {
        return requestMethod;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public HttpRequestMethod getHttpMethod() {
        return this.requestMethod;
    }
}
