package webserver.handling;

import webserver.http.HttpException;
import webserver.http.data.HttpRequest;
import webserver.http.enums.HttpRequestMethod;
import webserver.http.enums.HttpStatusCode;
import webserver.resources.StaticHandler;

import java.util.HashMap;
import java.util.Map;

// TODO: Path Variable 지원 생각해보기

/**
 * 요청에 대한 핸들러들의 매핑 정보를 담음
 * <p>
 * 정적 핸들러를 반환하도록 하는 것도 해당 매핑 클래스의 역할아닌가? 싶어서 일단 넣어봤다.
 */
public class RequestHandlerMapping {

    private final StaticHandler staticHandler;
    private final Map<String, Map<HttpRequestMethod, RequestHandler>> requestHandlerMap = new HashMap<>();

    public RequestHandlerMapping(StaticHandler staticHandler) {
        this.staticHandler = staticHandler;
    }

    public void registerRequestHandler(String URI, HttpRequestMethod httpRequestMethod, RequestHandler handler) {
        Map<HttpRequestMethod, RequestHandler> handlerMap = this.requestHandlerMap.getOrDefault(URI, null);

        if (handlerMap == null) {
            this.requestHandlerMap.put(URI, new HashMap<>());
            handlerMap = this.requestHandlerMap.get(URI);
        }

        handlerMap.put(httpRequestMethod, handler);
    }

    public RequestHandler getRequestHandler(HttpRequest httpRequest) throws HttpException {
        HttpRequestMethod method = httpRequest.requestMethod();
        String uri = httpRequest.requestURI();

        Map<HttpRequestMethod, RequestHandler> handlerMap =
            this.requestHandlerMap.getOrDefault(uri, null);

        if (handlerMap == null) {
            return staticHandler::handleStaticRouteRequest;
        }

        RequestHandler requestHandler = handlerMap.get(method);

        if (requestHandler == null) {
            throw new HttpException(HttpStatusCode.METHOD_NOT_ALLOWED, HttpStatusCode.METHOD_NOT_ALLOWED.getStatusName());
        }

        return requestHandler;
    }
}
