package webserver.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.http.data.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestParserTest {

    HttpRequestParser httpRequestParser = new HttpRequestParser();

    @Test
    @DisplayName("정상적인 POST 요청의 모든 요소를 완벽하게 파싱해야 한다 (Happy Path)")
    void parseStandardPostRequest() throws IOException {
        // given
        String requestBody = "userId=java&name=gildong";
        String rawRequest = "POST /user/create?version=1.1 HTTP/1.1\r\n" +
            "Host: localhost:8080\r\n" +
            "Connection: keep-alive\r\n" +
            "Content-Type: application/x-www-form-urlencoded\r\n" +
            "Content-Length: " + requestBody.length() + "\r\n" +
            "\r\n" +
            requestBody;

        InputStream in = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        // when
        HttpRequest request = httpRequestParser.parseRequestFromStream(in);

        // then
        assertAll(
            () -> assertEquals("POST", request.getRequestMethod().name()),
            () -> assertEquals("/user/create", request.getRequestURI()),

            // Query Parameter 확인
            () -> assertEquals("1.1", request.getQueryParameters().get("version")),

            // Version 확인
            () -> assertEquals("HTTP/1.1", request.getHttpVersion()),

            // Header 확인
            () -> assertEquals("localhost:8080", request.searchHeaderAttribute("Host")),
            () -> assertEquals("application/x-www-form-urlencoded", request.searchHeaderAttribute("Content-Type")),

            // Body 확인
            () -> assertNotNull(request.getBody()),
            () -> assertArrayEquals(requestBody.getBytes(), request.getBody().getData())
        );
    }

    @Test
    @DisplayName("Header의 키(Key)는 대소문자를 구분하지 않아야 한다 (Case Insensitive)")
    void headerKeyShouldBeCaseInsensitive() throws IOException {
        // given
        String rawRequest = "GET /index.html HTTP/1.1\r\n" +
            "HOST: localhost:8080\r\n" +
            "ConTent-LenGth: 0\r\n" +
            "\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        // when
        HttpRequest request = httpRequestParser.parseRequestFromStream(in);

        // then
        // 파서 구현부에서 key를 toLowerCase()로 저장하므로 소문자로 조회해도 찾아져야 함
        assertEquals("localhost:8080", request.searchHeaderAttribute("host"));
        assertEquals("0", request.searchHeaderAttribute("Content-Length"));
    }

    @Test
    @DisplayName("Header의 구분자(:) 뒤의 공백(Space)은 선택 사항(Optional)이어야 한다")
    void headerSeparatorSpaceIsOptional() throws IOException {
        // given
        // 'Host:localhost' (공백 없음), 'Connection:   keep-alive' (공백 많음)
        String rawRequest = "GET /index.html HTTP/1.1\r\n" +
            "Host:localhost:8080\r\n" +
            "Connection:   keep-alive\r\n" +
            "\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        // when
        HttpRequest request = httpRequestParser.parseRequestFromStream(in);

        // then
        // 구현 코드의 split(":", 2)와 trim() 덕분에 이 테스트는 정상 통과합니다.
        assertEquals("localhost:8080", request.searchHeaderAttribute("host"));
        assertEquals("keep-alive", request.searchHeaderAttribute("connection"));
    }

    @Test
    @DisplayName("Content-Length가 없는 요청도 정상적으로 파싱되어야 한다")
    void parseRequestWithoutContentLength() throws IOException {
        // given
        String rawRequest = "GET /index.html HTTP/1.1\r\n" +
            "Host: localhost:8080\r\n" +
            "\r\n";
        InputStream in = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        // when
        HttpRequest request = httpRequestParser.parseRequestFromStream(in);

        // then
        assertEquals("/index.html", request.getRequestURI());

        // 중요: 구현 코드에서 null이 아닌 HttpRequestBody.empty()를 반환하므로 assertNotNull이 맞음
        assertNotNull(request.getBody());
        // Body 내용이 비어있는지 확인 (HttpRequestBody 구현에 따라 메소드가 다를 수 있음, 예: getData().length == 0)
        assertEquals(0, request.getBody().getData().length);
    }
}