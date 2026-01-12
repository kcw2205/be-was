package webserver;

import db.UserDatabase;
import db.impl.UserDatabaseImpl;
import handler.IndexHandler;
import handler.UserHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.handling.RequestDispatcher;
import webserver.handling.RequestHandleThreadExecutor;
import webserver.handling.RequestHandlerMapping;
import webserver.handling.statics.StaticFileResolver;
import webserver.handling.statics.StaticHandler;
import webserver.http.HttpRequestParser;
import webserver.http.enums.HttpRequestMethod;
import webserver.session.SessionManager;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WebServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;
    private static final int CORE_POOL_SIZE = 200;
    private static final int MAX_POOL_SIZE = 200;
    private static final int QUEUE_CAPACITY = 1000;
    private static final long KEEP_ALIVE_TIME = 3;

    public static void main(String[] args) throws Exception {
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        var requestHandleThreadExecutor = getRequestHandleThreadExecutor();

        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            LOGGER.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                requestHandleThreadExecutor.createSession(connection);
            }
        }
    }

    // DI 해준 다음 쓰레드풀 실행객체 반환
    private static RequestHandleThreadExecutor getRequestHandleThreadExecutor() {
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        StaticFileResolver staticFileResolver = new StaticFileResolver();
        UserDatabase userDatabase = new UserDatabaseImpl();
        SessionManager sessionManager = new SessionManager();
        // static handlers
        StaticHandler staticHandler = new StaticHandler(staticFileResolver);
        RequestHandlerMapping requestHandlerMapping = new RequestHandlerMapping(staticHandler);

        // user-defined handlers
        UserHandler userHandler = new UserHandler(userDatabase, sessionManager);
        IndexHandler indexHandler = new IndexHandler(staticFileResolver, sessionManager);

        // request dispatcher
        RequestDispatcher requestDispatcher = new RequestDispatcher(requestHandlerMapping);


        // handler-mapping
        requestHandlerMapping.registerRequestHandler("/user/create", HttpRequestMethod.POST, userHandler::createUser);
        requestHandlerMapping.registerRequestHandler("/user/login", HttpRequestMethod.POST, userHandler::login);
        requestHandlerMapping.registerRequestHandler("/user/logout", HttpRequestMethod.POST, userHandler::logout);
        requestHandlerMapping.registerRequestHandler("/user/me", HttpRequestMethod.GET, userHandler::me);
        requestHandlerMapping.registerRequestHandler("/", HttpRequestMethod.GET, indexHandler::index);


        return new RequestHandleThreadExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(QUEUE_CAPACITY),
            new ThreadPoolExecutor.AbortPolicy(),
            httpRequestParser,
            requestDispatcher
        );
    }
}
