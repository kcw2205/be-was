package webserver.handling;

import webserver.handling.static_route.StaticHandler;
import webserver.http.data.HttpRequest;
import webserver.http.enums.HttpRequestMethod;

import java.util.HashMap;

/**
 * 요청에 대한 핸들러들의 매핑 정보를 담음
 * <p>
 * 정적 핸들러를 반환하도록 하는 것도 해당 매핑 클래스의 역할아닌가? 싶어서 일단 넣어봤다.
 */
public class RequestHandlerMapping {
    private final StaticHandler staticHandler;
    private final HashMap<String, RequestHandler> requestHandlerMap = new HashMap<>();

    public RequestHandlerMapping(StaticHandler staticHandler) {
        this.staticHandler = staticHandler;
    }

    // TODO: GET 요청만 배분할 수 있게 설정해둠. 확장할 수 있으면 확장하도록 하기.
    public void registerRequestHandler(String URI, HttpRequestMethod httpRequestMethod, RequestHandler interceptor) {
        this.requestHandlerMap.put(URI, interceptor);
    }

    public RequestHandler getRequestHandler(HttpRequest httpRequest) {
        if (this.requestHandlerMap.containsKey(httpRequest.getRequestURI())) {
            return this.requestHandlerMap.get(httpRequest.getRequestURI());
        }

        return staticHandler::handleStaticRouteRequest;
    }
}
