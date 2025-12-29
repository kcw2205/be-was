package webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpResponseFactory;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;

    public static void main(String args[]) throws Exception {
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        int corePoolSize = 2;
        int maxPoolSize = 4;
        int queueCapacity = 16;
        long keepAliveTime = 3;

        RequestHandlerExecutor requestHandlerExecutor =
                new RequestHandlerExecutor(new ThreadPoolExecutor(
                        corePoolSize,
                        maxPoolSize,
                        keepAliveTime,
                        TimeUnit.SECONDS,
                        new ArrayBlockingQueue<>(queueCapacity), // 꽉 찬 부분에 대한 대기 큐
                        new ThreadPoolExecutor.AbortPolicy()     // 꽉 차면 예외 발생(기본값)
                ), new HttpResponseFactory());

        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                // Thread thread = new Thread(new RequestHandler(connection));
                // thread.start();
                requestHandlerExecutor.createSession(connection);
            }
        }
    }
}
