package webserver.handling;

import webserver.handling.static_route.StaticHandler;
import webserver.http.data.HttpRequest;
import webserver.http.enums.HttpRequestMethod;

import java.util.HashMap;

// TODO: Path Variable 지원 생각해보기

/**
 * 요청에 대한 핸들러들의 매핑 정보를 담음
 * <p>
 * 정적 핸들러를 반환하도록 하는 것도 해당 매핑 클래스의 역할아닌가? 싶어서 일단 넣어봤다.
 */
public class RequestHandlerMapping {
    private final StaticHandler staticHandler;
    // TODO: 405 (경로는 있으나, Method는 없는 경우) 를 지원해주어야함
    private final HashMap<String, RequestHandler> requestHandlerMap = new HashMap<>();

    public RequestHandlerMapping(StaticHandler staticHandler) {
        this.staticHandler = staticHandler;
    }

    public void registerRequestHandler(String URI, HttpRequestMethod httpRequestMethod, RequestHandler interceptor) {
        this.requestHandlerMap.put(httpRequestMethod.toString() + " " + URI, interceptor);
    }

    public RequestHandler getRequestHandler(HttpRequest httpRequest) {
        String fullRequestURI = httpRequest.getHttpMethod().toString() + " " + httpRequest.getRequestURI();

        return this.requestHandlerMap.getOrDefault(fullRequestURI, staticHandler::handleStaticRouteRequest);
    }
}
