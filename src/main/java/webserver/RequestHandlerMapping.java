package webserver;

import webserver.data.HttpRequest;
import webserver.data.HttpResponse;
import webserver.data.ResponseEntity;
import webserver.data.enums.HttpRequestMethod;
import webserver.factory.HttpResponseFactory;
import webserver.interceptor.RequestInterceptor;

import java.util.HashMap;

public class RequestHandlerMapping {
    private final HashMap<String, RequestInterceptor> requestHandlerMap = new HashMap<>();

    public RequestHandlerMapping(
            RequestInterceptor staticRouteInterceptor
    ) {
        this.registerGetMapping("/registration", HttpRequestMethod.GET, staticRouteInterceptor);
        this.registerGetMapping("/", HttpRequestMethod.GET, staticRouteInterceptor);
        this.registerGetMapping("/mypage", HttpRequestMethod.GET, staticRouteInterceptor);
        this.registerGetMapping("/article", HttpRequestMethod.GET, staticRouteInterceptor);
        this.registerGetMapping("/comment", HttpRequestMethod.GET, staticRouteInterceptor);
        this.registerGetMapping("/login", HttpRequestMethod.GET, staticRouteInterceptor);
    }

    // TODO: GET 요청만 배분할 수 있게 설정해둠. 확장할 수 있으면 확장하도록 하기.
    public void registerGetMapping(String URI, HttpRequestMethod httpRequestMethod, RequestInterceptor interceptor) {
        this.requestHandlerMap.put(URI, interceptor);
    }

    public RequestInterceptor getInterceptorByRequestURI(String uri) {
        return this.requestHandlerMap.get(uri);
    }
}
