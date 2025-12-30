package webserver.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.data.enums.HttpRequestMethod;
import webserver.data.HttpRequest;
import webserver.data.HttpResponse;
import webserver.data.ResponseEntity;
import webserver.factory.HttpResponseFactory;

import java.util.HashMap;

/**
 * 특정 요청에 대한 비즈니스 로직을 수행하기 위해 요청 처리를 가로채는 부분을 담당
 * 매핑된
 */
public class RequestInterceptorMapper {
    private final Logger logger = LoggerFactory.getLogger(RequestInterceptorMapper.class.getName());
    private final HashMap<String, RequestInterceptor> interceptorHashMap = new HashMap<>();
    private final RequestInterceptor staticFileInterceptor;
    private final HttpResponseFactory httpResponseFactory;

    public RequestInterceptorMapper(HttpResponseFactory httpResponseFactory, RequestInterceptor staticFileInterceptor, RequestInterceptor staticRouteInterceptor) {
        this.httpResponseFactory = httpResponseFactory;
        this.staticFileInterceptor = staticFileInterceptor;
        this.registerGetMapping("/registration", HttpRequestMethod.GET, staticRouteInterceptor);
        this.registerGetMapping("/", HttpRequestMethod.GET, staticRouteInterceptor);
        this.registerGetMapping("/mypage", HttpRequestMethod.GET, staticRouteInterceptor);
        this.registerGetMapping("/article", HttpRequestMethod.GET, staticRouteInterceptor);
        this.registerGetMapping("/comment", HttpRequestMethod.GET, staticRouteInterceptor);
        this.registerGetMapping("/login", HttpRequestMethod.GET, staticRouteInterceptor);
    }

    // TODO: GET 요청만 가로챌 수 있게 설정해둠. 확장할 수 있으면 확장하도록 하기.
    public void registerGetMapping(String URI, HttpRequestMethod httpRequestMethod, RequestInterceptor interceptor) {
        this.interceptorHashMap.put(URI, interceptor);
    }

    /**
     * HttpRequest 를 확인하여 등록된 요청 형식에 대해 HttpResponse를 사전에 만들어주는 함수
     */
    public HttpResponse intercept(HttpRequest httpRequest) {
        RequestInterceptor interceptor = this.interceptorHashMap.get(httpRequest.getRequestURI());

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
