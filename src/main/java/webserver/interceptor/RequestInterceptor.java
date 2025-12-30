package webserver.interceptor;

import webserver.data.HttpRequest;
import webserver.data.ResponseEntity;

public interface RequestInterceptor {

    public ResponseEntity handleRequest(HttpRequest httpRequest);
}
