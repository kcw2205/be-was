package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import webserver.interceptor.RequestInterceptorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.data.enums.HttpStatusCode;
import webserver.data.HttpRequest;
import webserver.data.HttpResponse;
import webserver.factory.HttpRequestFactory;
import webserver.factory.HttpResponseFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final HttpResponseFactory httpResponseFactory;
    private final HttpRequestFactory httpRequestFactory;
    private final RequestInterceptorMapper requestInterceptorMapper;

    public RequestHandler(
            Socket connectionSocket,
            HttpRequestFactory httpRequestFactory,
            HttpResponseFactory httpResponseFactory,
            RequestInterceptorMapper requestInterceptorMapper
    ) {
        this.httpResponseFactory = httpResponseFactory;
        this.httpRequestFactory = httpRequestFactory;
        this.connection = connectionSocket;
        this.requestInterceptorMapper = requestInterceptorMapper;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        // TODO: Keep alive 에 대한 옵션이 있는데 리소스를 정리해도 되는가?
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream();) {

            HttpRequest httpRequest = httpRequestFactory.parseRequestFromStream(in);
            HttpResponse httpResponse = null;

            httpResponse = requestInterceptorMapper.intercept(httpRequest);

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
