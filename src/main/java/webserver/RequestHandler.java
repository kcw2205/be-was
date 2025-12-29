package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpResponse;
import webserver.http.HttpResponseFactory;
import webserver.http.HttpStatus;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private DataOutputStream outputStream;
    private final HttpResponseFactory responseFactory;

    public RequestHandler(Socket connectionSocket, HttpResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
        this.connection = connectionSocket;

//        try (OutputStream out = connection.getOutputStream()) {
//            outputStream = new DataOutputStream(out);
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            // TODO: 1단계 설계대로 개선 필요
            DataOutputStream dos = new DataOutputStream(out);

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/index.html");

            if (inputStream == null) {
                throw new IllegalStateException("Resource not found");
            }

            byte[] body = inputStream.readAllBytes();

            response200Header(dos, body.length);
            responseBody(dos, body);
            inputStream.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
