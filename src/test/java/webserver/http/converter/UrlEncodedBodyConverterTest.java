package webserver.http.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.http.data.HttpRequestBody;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UrlEncodedBodyConverterTest {

    private final UrlEncodedBodyConverter converter = new UrlEncodedBodyConverter();

    @Test
    @DisplayName("일반적인 쿼리 스트링을 올바르게 파싱해야 한다")
    void convertStandardQueryString() {
        // given
        String data = "userId=java&password=password123&name=gildong";
        HttpRequestBody body = new HttpRequestBody(data.getBytes(StandardCharsets.UTF_8));

        // when
        Map<String, String> result = converter.convertFromBody(body);

        // then
        assertAll(
                () -> assertEquals(3, result.size()),
                () -> assertEquals("java", result.get("userId")),
                () -> assertEquals("password123", result.get("password")),
                () -> assertEquals("gildong", result.get("name"))
        );
    }

    @Test
    @DisplayName("URL 인코딩된 값(특수문자, 공백 등)을 디코딩하여 파싱해야 한다")
    void convertEncodedValues() {
        // given
        // "name=홍 길동&email=test@example.com" (공백 -> %20, @ -> %40)
        // 실제 브라우저는 공백을 + 로 보내기도 하지만, URLEncoder는 기본적으로 변환함.
        // 여기서는 "홍 길동" -> "%ED%99%8D+%EA%B8%B8%EB%8F%99", "test@example.com" -> "test%40example.com"
        String data = "name=%ED%99%8D+%EA%B8%B8%EB%8F%99&email=test%40example.com";
        HttpRequestBody body = new HttpRequestBody(data.getBytes(StandardCharsets.UTF_8));

        // when
        Map<String, String> result = converter.convertFromBody(body);

        // then
        assertAll(
                () -> assertEquals("홍 길동", result.get("name")),
                () -> assertEquals("test@example.com", result.get("email"))
        );
    }

    @Test
    @DisplayName("값이 비어있는 키(key=)도 처리해야 한다")
    void convertEmptyValue() {
        // given
        String data = "userId=java&description=";
        HttpRequestBody body = new HttpRequestBody(data.getBytes(StandardCharsets.UTF_8));

        // when
        Map<String, String> result = converter.convertFromBody(body);

        // then
        assertAll(
                () -> assertEquals("java", result.get("userId")),
                () -> assertEquals("", result.get("description"), "값이 없는 경우 빈 문자열이어야 함")
        );
    }

    @Test
    @DisplayName("단일 키-값 쌍만 있는 경우도 파싱해야 한다")
    void convertSinglePair() {
        // given
        String data = "key=value";
        HttpRequestBody body = new HttpRequestBody(data.getBytes(StandardCharsets.UTF_8));

        // when
        Map<String, String> result = converter.convertFromBody(body);

        // then
        assertEquals("value", result.get("key"));
    }
}
