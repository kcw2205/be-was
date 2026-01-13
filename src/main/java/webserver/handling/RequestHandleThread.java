package webserver.handling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpRequestParser;
import webserver.http.data.HttpRequest;
import webserver.http.data.HttpResponse;
import webserver.http.enums.HttpHeaderKey;
import webserver.http.enums.HttpStatusCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class RequestHandleThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandleThread.class);

    private final Socket connection;
    private final HttpRequestParser httpRequestParser;
    private final RequestDispatcher requestDispatcher;

    public RequestHandleThread(
        Socket connectionSocket,
        HttpRequestParser httpRequestParser,
        RequestDispatcher requestDispatcher
    ) {
        this.httpRequestParser = httpRequestParser;
        this.connection = connectionSocket;
        this.requestDispatcher = requestDispatcher;
    }

    public void run() {
        LOGGER.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream()) {

            connection.setSoTimeout(2000);

            while (true) {
                try {
                    long startTime = System.currentTimeMillis();

                    HttpRequest httpRequest = httpRequestParser.parseRequestFromStream(in);
                    HttpResponse httpResponse = requestDispatcher.dispatch(httpRequest)
                        .addHeader(HttpHeaderKey.CONNECTION, "keep-alive")
                        .addHeader(HttpHeaderKey.KEEP_ALIVE, "timeout=2, max=100") // 2초 유지, 최대 100회 요청
                        .toHttpResponse();

                    out.write(httpResponse.serialize());
                    out.flush();

                    long endTime = System.currentTimeMillis();

                    LOGGER.debug("Successfully handled request of " + httpRequest.requestMethod() + " " + httpRequest.requestURI());
                    LOGGER.debug("Time elasped : " + (endTime - startTime) + "ms");

                    if (httpRequest.searchHeaderAttribute(HttpHeaderKey.CONNECTION.toString()).equals("close")) {

                        break;
                    }

                } catch (RuntimeException e) {
                    LOGGER.error(e.getMessage(), e);
                    out.write(ResponseEntity.simple(HttpStatusCode.INTERNAL_SERVER_ERROR).toHttpResponse().serialize());
                    out.flush();
                    break;
                } catch (SocketTimeoutException e) {
                    break;
                }

                // TODO: keep alive 적용 시 쓰레드풀이 부족할 경우 무조건 살아있는 시간만큼 기다려야함.
                // break;
            }

        } catch (IOException e) {
            LOGGER.debug("Socket IO Exception while reading request stream", e);
        }

    }
}
