package webserver.http.data;

import webserver.http.enums.HttpRequestMethod;

import java.util.Map;
import java.util.Optional;

public record HttpRequest(
    HttpRequestMethod requestMethod,
    String requestURI,
    String httpVersion,
    Map<String, String> headers,
    Map<String, Cookie> cookies,
    Map<String, String> queryParameters,
    HttpRequestBody body
) {
    public String searchHeaderAttribute(String attributeName) {
        attributeName = attributeName.toLowerCase();
        return this.headers.getOrDefault(attributeName, null);
    }

    public Optional<Cookie> getCookieByName(String attributeName) {
        return Optional.ofNullable(cookies.getOrDefault(attributeName, null));
    }

    public HttpRequestMethod getHttpMethod() {
        return this.requestMethod;
    }
}
