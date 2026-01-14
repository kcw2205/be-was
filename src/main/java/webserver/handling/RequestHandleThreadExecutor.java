package webserver.handling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpRequestParser;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// TODO: 요청 핸들링 과정에서 생기는 책임 결합을 SRP에 따라 분리하기
public class RequestHandleThreadExecutor extends ThreadPoolExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandleThreadExecutor.class);
    private final HttpRequestParser httpRequestParser;
    private final RequestDispatcher requestDispatcher;

    public RequestHandleThreadExecutor(
        int corePoolSize,
        int maximumPoolSize,
        long keepAliveTime,
        TimeUnit unit,
        BlockingQueue<Runnable> workQueue,
        RejectedExecutionHandler handler,
        HttpRequestParser httpRequestParser,
        RequestDispatcher requestDispatcher
    ) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        this.httpRequestParser = httpRequestParser;
        this.requestDispatcher = requestDispatcher;
    }

    public void createSession(Socket socket) {
        try {
            this.execute(new RequestHandleThread(socket, httpRequestParser, requestDispatcher));
        } catch (RejectedExecutionException e) {
            LOGGER.error("RejectedExecutionException in RequestHandleThreadExecutor", e);
        }
    }
}
