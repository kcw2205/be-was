package webserver;

import webserver.dispatcher.RequestDispatcher;
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
    private final RequestDispatcher requestDispatcher;

    public RequestHandlerExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
        this.httpResponseFactory = new HttpResponseFactory();
        this.httpRequestFactory = new HttpRequestFactory();
        this.requestDispatcher = new RequestDispatcher(
                httpResponseFactory,
                new StaticFileInterceptor(),
                new RequestHandlerMapping(new StaticRouteInterceptor())
        );
    }

    public void createSession(Socket socket) {
        try {
            executor.execute(new RequestHandler(socket, httpRequestFactory, httpResponseFactory, requestDispatcher));
        } catch (RejectedExecutionException e) {
            logger.error("Thread Pool Rejected Execution");
            e.printStackTrace();
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
