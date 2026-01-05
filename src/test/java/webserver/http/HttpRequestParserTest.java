package webserver.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.http.data.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestParserTest {

    HttpRequestParser httpRequestParser = new HttpRequestParser();

    @Test
    @DisplayName("정상적인 POST 요청의 모든 요소를 완벽하게 파싱해야 한다 (Happy Path)")
    void parseStandardPostRequest() {
        String requestBody = "userId=java&name=gildong";
        String rawRequest = "POST /user/create?version=1.1 HTTP/1.1\r\n" +
            "Host: localhost:8080\r\n" +
            "Connection: keep-alive\r\n" +
            "Content-Type: application/x-www-form-urlencoded\r\n" +
            "Content-Length: " + requestBody.length() + "\r\n" +
            "\r\n" +
            requestBody;

        InputStream in = null;

        try {
            in = new ByteArrayInputStream(rawRequest.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        HttpRequest request = httpRequestParser.parseRequestFromStream(in);

        assertAll(
            () -> assertEquals("POST", request.getRequestMethod().name()),
            () -> assertEquals("/user/create", request.getRequestURI()),

            () -> assertEquals("1.1", request.getQueryParameters().get("version")),
            () -> assertEquals("HTTP/1.1", request.getHttpVersion()),

            () -> assertEquals("localhost:8080", request.searchHeaderAttribute("Host")),
            () -> assertEquals("application/x-www-form-urlencoded", request.searchHeaderAttribute("Content-Type")),

            () -> assertNotNull(request.getBody()),
            () -> assertArrayEquals(requestBody.getBytes(), request.getBody().getContent())
        );
    }

    @Test
    @DisplayName("Header의 키(Key)는 대소문자를 구분하지 않아야 한다 (Case Insensitive)")
    void headerKeyShouldBeCaseInsensitive() {
        String rawRequest = "GET /index.html HTTP/1.1\r\n" +
            "HOST: localhost:8080\r\n" +
            "ConTent-LenGth: 0\r\n" +
            "\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = httpRequestParser.parseRequestFromStream(in);

        // TODO: case insensitive 한 경우라서 데이터 구조를 바꾸어야 함.
        assertNotNull(request.searchHeaderAttribute("host"));
        assertEquals("localhost:8080", request.searchHeaderAttribute("Host"));
        assertEquals("0", request.searchHeaderAttribute("Content-Length"));
    }

    @Test
    @DisplayName("Header의 구분자(:) 뒤의 공백(Space)은 선택 사항(Optional)이어야 한다")
    void headerSeparatorSpaceIsOptional() {
        // given
        // 'Host:localhost' (공백 없음), 'Connection:   keep-alive' (공백 많음)
        String rawRequest = "GET /index.html HTTP/1.1\r\n" +
            "Host: localhost:8080   \r\n" +
            "Connection:   keep-alive\r\n" +
            "\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        // when
        HttpRequest request = httpRequestParser.parseRequestFromStream(in);

        // then
        // 현재 구현체의 split(":\\s+") 정규식은 공백이 없으면 쪼개지 못해 에러가 발생할 것입니다. (수정 필요 포인트)
        assertEquals("localhost:8080", request.searchHeaderAttribute("host"));
        assertEquals("keep-alive", request.searchHeaderAttribute("connection"));
    }

    @Test
    @DisplayName("Content-Length가 없는 요청도 정상적으로 파싱되어야 한다")
    void parseRequestWithoutContentLength() {
        String rawRequest = "GET /index.html HTTP/1.1\r\n" +
            "Host: localhost:8080\r\n" +
            "\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = httpRequestParser.parseRequestFromStream(in);

        assertEquals("/index.html", request.getRequestURI());
        assertNull(request.getBody()); // Body는 null이어야 함
    }
}