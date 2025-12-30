package webserver;

import webserver.interceptor.RequestInterceptorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.factory.HttpRequestFactory;
import webserver.factory.HttpResponseFactory;
import webserver.interceptor.StaticFileInterceptor;
import webserver.interceptor.StaticRouteInterceptor;

import java.net.Socket;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

public class RequestHandlerExecutor {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerExecutor.class);
    private final ThreadPoolExecutor executor;
    private final HttpRequestFactory httpRequestFactory;
    private final HttpResponseFactory httpResponseFactory;
    private final RequestInterceptorMapper requestInterceptorMapper;

    public RequestHandlerExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
        this.httpResponseFactory = new HttpResponseFactory();
        this.httpRequestFactory = new HttpRequestFactory();
        this.requestInterceptorMapper = new RequestInterceptorMapper(
                httpResponseFactory,
                new StaticFileInterceptor(),
                new StaticRouteInterceptor()
        );
    }

    public void createSession(Socket socket) {
        try {
            executor.execute(new RequestHandler(socket, httpRequestFactory, httpResponseFactory, requestInterceptorMapper));
        } catch (RejectedExecutionException e) {
            logger.error("Thread Pool Rejected Execution");
            e.printStackTrace();
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
