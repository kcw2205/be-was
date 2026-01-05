package webserver;

import db.UserDatabase;
import handler.StaticFileHandler;
import handler.StaticRouteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.factory.HttpRequestFactory;
import webserver.factory.HttpResponseFactory;

import java.net.Socket;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

public class RequestHandleThreadExecutor {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandleThreadExecutor.class);
    private final ThreadPoolExecutor executor;
    private final HttpRequestFactory httpRequestFactory;
    private final HttpResponseFactory httpResponseFactory;
    private final RequestDispatcher requestDispatcher;

    public RequestHandleThreadExecutor(ThreadPoolExecutor executor, UserDatabase userDatabase) {
        this.executor = executor;
        this.httpResponseFactory = new HttpResponseFactory();
        this.httpRequestFactory = new HttpRequestFactory();
        this.requestDispatcher = new RequestDispatcher(
            httpResponseFactory,
            new StaticFileHandler(),
            new RequestHandlerMapping(new StaticRouteHandler(), userDatabase)
        );
    }

    public void createSession(Socket socket) {
        try {
            executor.execute(new RequestHandleThread(socket, httpRequestFactory, httpResponseFactory, requestDispatcher));
        } catch (RejectedExecutionException e) {
            logger.error("Thread Pool Rejected Execution");
            e.printStackTrace();
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
