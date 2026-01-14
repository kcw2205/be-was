package webserver;

import dao.ArticleDAO;
import dao.CommentDAO;
import dao.UserDAO;
import dao.impl.h2.ArticleDAOH2;
import dao.impl.h2.CommentDAOH2;
import dao.impl.h2.UserDAOH2;
import db.H2DatabaseConfig;
import db.JDBCConnectionManager;
import handler.ImageUploadHandler;
import handler.UserHandler;
import handler.ViewHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ArticleService;
import service.CommentService;
import service.ImageUploadService;
import service.UserService;
import service.impl.ArticleServiceImpl;
import service.impl.CommentServiceImpl;
import service.impl.ImageUploadServiceImpl;
import service.impl.UserServiceImpl;
import webserver.handling.RequestDispatcher;
import webserver.handling.RequestHandleThreadExecutor;
import webserver.handling.RequestHandlerMapping;
import webserver.http.HttpRequestParser;
import webserver.http.enums.HttpRequestMethod;
import webserver.resources.ImageUploadWorker;
import webserver.resources.StaticFileResolver;
import webserver.resources.StaticHandler;
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

    // TODO: DI 해준 다음 쓰레드풀 실행객체 반환
    // TODO: 강하게 결합된 RequestHandling 로직 책임 위주로 분리하기
    private static RequestHandleThreadExecutor getRequestHandleThreadExecutor() {
        // webserver request handling dependencies
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        StaticFileResolver staticFileResolver = new StaticFileResolver();
        ImageUploadWorker imageUploadWorker = new ImageUploadWorker();

        // database related dependencies
        H2DatabaseConfig h2DatabaseConfig = new H2DatabaseConfig();
        JDBCConnectionManager jdbcConnectionManager = new JDBCConnectionManager();

        // static handlers
        StaticHandler staticHandler = new StaticHandler(staticFileResolver);
        RequestHandlerMapping requestHandlerMapping = new RequestHandlerMapping(staticHandler);

        // request dispatcher
        RequestDispatcher requestDispatcher = new RequestDispatcher(requestHandlerMapping);

        // user-defined data access objects
        UserDAO userDAO = new UserDAOH2(h2DatabaseConfig, jdbcConnectionManager);
        ArticleDAO articleDAO = new ArticleDAOH2(h2DatabaseConfig, jdbcConnectionManager);
        CommentDAO commentDAO = new CommentDAOH2(h2DatabaseConfig, jdbcConnectionManager);
        SessionManager sessionManager = new SessionManager();


        // user-defined service layers
        UserService userService = new UserServiceImpl(userDAO, sessionManager);
        ArticleService articleService = new ArticleServiceImpl(articleDAO);
        CommentService commentService = new CommentServiceImpl(commentDAO, userDAO);
        ImageUploadService imageUploadService = new ImageUploadServiceImpl(imageUploadWorker);

        // user-defined handlers

        UserHandler userHandler = new UserHandler(userService);
        ViewHandler viewHandler = new ViewHandler(staticHandler, userService, articleService, commentService);
        ImageUploadHandler imageUploadHandler = new ImageUploadHandler(imageUploadService);

        // do handler-mapping
        requestHandlerMapping.registerRequestHandler("/user/create", HttpRequestMethod.POST, userHandler::createUser);
        requestHandlerMapping.registerRequestHandler("/user/login", HttpRequestMethod.POST, userHandler::login);
        requestHandlerMapping.registerRequestHandler("/user/logout", HttpRequestMethod.POST, userHandler::logout);
        requestHandlerMapping.registerRequestHandler("/user/me", HttpRequestMethod.GET, userHandler::me);
        requestHandlerMapping.registerRequestHandler("/", HttpRequestMethod.GET, viewHandler::indexPage);
        requestHandlerMapping.registerRequestHandler("/mypage", HttpRequestMethod.GET, viewHandler::myPage);
        requestHandlerMapping.registerRequestHandler("/image/upload", HttpRequestMethod.POST, imageUploadHandler::uploadFile);
        requestHandlerMapping.registerRequestHandler("/post/write", HttpRequestMethod.GET, viewHandler::writePage);

        // return entrypoint dependency
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
