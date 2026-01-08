package webserver.http.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.http.enums.HttpStatusCode;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpResponseTest {

    @Test
    @DisplayName("바디가 있는 응답이 올바르게 직렬화되어야 한다")
    void serializeWithBody() {
        // given
        HttpStatusCode status = HttpStatusCode.OK;
        String version = "HTTP/1.1";
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8");
        headers.put("Content-Length", "12");
        byte[] body = "Hello World!".getBytes(StandardCharsets.UTF_8);

        HttpResponse response = new HttpResponse(status, version, headers, body);

        // when
        byte[] serialized = response.serialize();
        String result = new String(serialized, StandardCharsets.UTF_8);

        // then
        String expected = "HTTP/1.1 200 Ok\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 12\r\n" +
                "\r\n" +
                "Hello World!";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("바디가 없는 응답이 올바르게 직렬화되어야 한다")
    void serializeWithoutBody() {
        // given
        HttpStatusCode status = HttpStatusCode.REDIRECT;
        String version = "HTTP/1.1";
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", "/index.html");
        byte[] body = null;

        HttpResponse response = new HttpResponse(status, version, headers, body);

        // when
        byte[] serialized = response.serialize();
        String result = new String(serialized, StandardCharsets.UTF_8);

        // then
        String expected = "HTTP/1.1 302 Redirect\r\n" +
                "Location: /index.html\r\n" +
                "\r\n";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("헤더가 없는 응답이 올바르게 직렬화되어야 한다")
    void serializeWithoutHeaders() {
        // given
        HttpStatusCode status = HttpStatusCode.OK;
        String version = "HTTP/1.1";
        Map<String, String> headers = new LinkedHashMap<>();
        byte[] body = null;

        HttpResponse response = new HttpResponse(status, version, headers, body);

        // when
        byte[] serialized = response.serialize();
        String result = new String(serialized, StandardCharsets.UTF_8);

        // then
        String expected = "HTTP/1.1 200 Ok\r\n" +
                "\r\n";
        assertEquals(expected, result);
    }
}
