package webserver.handling;

import webserver.http.HttpException;
import webserver.http.data.HttpRequest;
import webserver.http.enums.HttpContentType;

/**
 * 특정 요청에 대한 비즈니스 로직을 수행하기 위해 요청 처리를 가로채는 부분을 담당
 * 매핑된
 */
public class RequestDispatcher {
    private final RequestHandlerMapping requestHandlerMapping;

    public RequestDispatcher(RequestHandlerMapping requestHandlerMapping) {
        this.requestHandlerMapping = requestHandlerMapping;
    }

    /**
     * HttpRequest 를 확인하여 등록된 요청 형식에 대해 HttpResponse를 사전에 만들어주는 함수
     */
    public ResponseEntity<?> dispatch(HttpRequest httpRequest) {

        try {
            RequestHandler handler = this.requestHandlerMapping.getRequestHandler(httpRequest);
            return handler.handle(httpRequest);
        } catch (HttpException e) {
            // TODO: 당장은 Message 만 반환. 커스텀할 수 있도록 하는 것은 나중에 제공.
            return ResponseEntity.create(e.getMessage(), e.getStatusCode(), HttpContentType.TEXT_PLAIN);
        }
    }
}
