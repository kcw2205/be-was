package webserver.handling;

import webserver.http.data.HttpRequest;

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
        RequestHandler interceptor = this.requestHandlerMapping
            .getRequestHandler(httpRequest);

        return interceptor.handle(httpRequest);
    }
}
