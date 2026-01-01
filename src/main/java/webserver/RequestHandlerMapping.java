package webserver;

import db.UserDatabase;
import webserver.data.enums.HttpRequestMethod;
import handler.RequestHandler;
import handler.UserHandler;

import java.util.HashMap;

public class RequestHandlerMapping {
    private final HashMap<String, RequestHandler> requestHandlerMap = new HashMap<>();

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
    public void registerGetMapping(String URI, HttpRequestMethod httpRequestMethod, RequestHandler interceptor) {
        this.requestHandlerMap.put(URI, interceptor);
    }

    public RequestHandler getInterceptorByRequestURI(String uri) {
        return this.requestHandlerMap.get(uri);
    }
}
