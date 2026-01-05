package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpRequestParser;
import webserver.http.HttpResponseFactory;
import webserver.http.data.HttpRequest;
import webserver.http.data.HttpResponse;
import webserver.http.enums.HttpStatusCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandleThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandleThread.class);

    private final Socket connection;
    private final HttpResponseFactory httpResponseFactory;
    private final HttpRequestParser httpRequestParser;
    private final RequestDispatcher requestDispatcher;

    public RequestHandleThread(
        Socket connectionSocket,
        HttpRequestParser httpRequestParser,
        HttpResponseFactory httpResponseFactory,
        RequestDispatcher requestDispatcher
    ) {
        this.httpResponseFactory = httpResponseFactory;
        this.httpRequestParser = httpRequestParser;
        this.connection = connectionSocket;
        this.requestDispatcher = requestDispatcher;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

        // TODO: Keep alive 에 대한 옵션이 있는데 리소스를 정리해도 되는가?
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream();) {

            HttpRequest httpRequest = httpRequestParser.parseRequestFromStream(in);
            HttpResponse httpResponse = null;

            httpResponse = requestDispatcher.intercept(httpRequest);

            if (httpResponse == null) {
                httpResponse = httpResponseFactory
                    .createResponse(
                        httpRequest.getHttpVersion(),
                        HttpStatusCode.INTERNAL_SERVER_ERROR,
                        "Request Mapping not found",
                        "text/plain;charset=utf-8");
            }

            out.write(httpResponse.serialize());
            out.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
