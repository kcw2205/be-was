package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.data.HttpRequest;
import webserver.data.HttpResponse;
import webserver.data.ResponseEntity;
import webserver.factory.HttpResponseFactory;
import handler.RequestHandler;

/**
 * 특정 요청에 대한 비즈니스 로직을 수행하기 위해 요청 처리를 가로채는 부분을 담당
 * 매핑된
 */
public class RequestDispatcher {
    private final Logger logger = LoggerFactory.getLogger(RequestDispatcher.class.getName());
    private final RequestHandlerMapping requestHandlerMapping;
    private final RequestHandler staticFileInterceptor;
    private final HttpResponseFactory httpResponseFactory;

    public RequestDispatcher(HttpResponseFactory httpResponseFactory, RequestHandler staticFileInterceptor, RequestHandlerMapping requestHandlerMapping) {
        this.httpResponseFactory = httpResponseFactory;
        this.staticFileInterceptor = staticFileInterceptor;
        this.requestHandlerMapping = requestHandlerMapping;
    }
    /**
     * HttpRequest 를 확인하여 등록된 요청 형식에 대해 HttpResponse를 사전에 만들어주는 함수
     */
    public HttpResponse intercept(HttpRequest httpRequest) {
        RequestHandler interceptor = this.requestHandlerMapping
                .getInterceptorByRequestURI(httpRequest.getRequestURI());

        ResponseEntity entity = this.staticFileInterceptor.handleRequest(httpRequest);

        if (entity != null) {
            return httpResponseFactory.createResponse(
                    httpRequest.getHttpVersion(),
                    entity.getHttpStatusCode(),
                    entity.getSerializedData(),
                    entity.getContentType()
            );
        }

        if (interceptor != null) {
             entity = interceptor.handleRequest(httpRequest);
             return httpResponseFactory.createResponse(
                     httpRequest.getHttpVersion(),
                     entity.getHttpStatusCode(),
                     entity.getSerializedData(),
                     entity.getContentType()
             );
        }

        return null;
    }
}
