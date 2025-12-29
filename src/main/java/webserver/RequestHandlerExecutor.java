package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpResponseFactory;

import java.net.Socket;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

public class RequestHandlerExecutor {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerExecutor.class);
    private final ThreadPoolExecutor executor;
    private final HttpResponseFactory httpResponseFactory;

    public RequestHandlerExecutor(ThreadPoolExecutor executor, HttpResponseFactory httpResponseFactory) {
        this.executor = executor;
        this.httpResponseFactory = httpResponseFactory;
    }

    public void createSession(Socket socket) {
        try {
            executor.execute(new RequestHandler(socket, httpResponseFactory));
        } catch (RejectedExecutionException e) {
            logger.error("Thread Pool Rejected Execution");
            e.printStackTrace();
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
