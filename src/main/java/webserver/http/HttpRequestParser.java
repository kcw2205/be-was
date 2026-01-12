package webserver.http;

import webserver.http.data.Cookie;
import webserver.http.data.HttpRequest;
import webserver.http.data.HttpRequestBody;
import webserver.http.enums.HttpHeaderKey;
import webserver.http.enums.HttpRequestMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 바이트 배열로부터 파싱해서 이해하기 쉬운 HttpRequest 객체로 바꾸어주는 역할을 하는 클래스
 * <p>
 * 후에 멀티파트같은 부분도 파싱해서 병합해주는 것도 이곳에서 할 수 있도록 하면 좋을 듯 하다.
 */
// TODO: 자체적으로 InputStream으로부터 `\r\n` 까지 읽어들이는 커스텀 유틸리티가 필요. 이를 이용해 InputStream만 사용하도록 개선필요.
public class HttpRequestParser {

    public HttpRequest parseRequestFromStream(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // TODO: null 로 넣는게 좋은 방법일까? 다만 굳이 생성자 오버로딩을 해야할까?
        String[] requestMethodAndURI = parseFirstLine(bufferedReader);
        Map<String, String> headers = parseHeader(bufferedReader);

        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        String contentType = headers.get("content-type");
        String cookieStr = headers.getOrDefault(HttpHeaderKey.COOKIE.toString().toLowerCase(), "");

        String[] requestUri = requestMethodAndURI[1].split("\\?");

        return new HttpRequest(
            HttpRequestMethod.valueOf(requestMethodAndURI[0]),
            requestUri[0],
            requestMethodAndURI[2],
            headers,
            parseCookies(cookieStr),
            parseQueryParameters(URLDecoder.decode(requestMethodAndURI[1], "UTF-8")),
            contentLength != 0 ? parseBody(contentLength, bufferedReader) : HttpRequestBody.empty()
        );
    }

    private String[] parseFirstLine(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();

        if (line == null) {
            throw new IOException("Invalid HTTP Request");
        }

        String[] requestMethodAndURI = line.split(" ");

        if (requestMethodAndURI.length != 3) {
            throw new IOException("Invalid request method");
        }

        return requestMethodAndURI;
    }

    private Map<String, String> parseHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> map = new HashMap<>();

        String line = bufferedReader.readLine();

        while (line != null) {

            if (line.isEmpty()) { // 빈 라인을 찾았을 경우 Header 의 끝이다.
                return map;
            }

            String[] tokens = line.split(":", 2);

            if (tokens.length != 2) {
                throw new IOException("Invalid Request Format.");
            }

            map.put(tokens[0].toLowerCase(), tokens[1].trim());

            line = bufferedReader.readLine();
        }

        return map;

    }

    private HttpRequestBody parseBody(int contentLength, BufferedReader bufferedReader) throws IOException {
        char[] cbuf = new char[contentLength];
        byte[] bytes = new byte[contentLength];
        bufferedReader.read(cbuf, 0, contentLength);

        for (int i = 0; i < contentLength; i++) {
            bytes[i] = (byte) cbuf[i];
        }

        return new HttpRequestBody(bytes);

    }

    // TODO: UrlEncodeParser 활용하기
    private Map<String, String> parseQueryParameters(String uri) {
        var queryParameters = new HashMap<String, String>();

        String[] split = uri.split("\\?");

        if (split.length < 2) {
            return queryParameters;
        }

        String[] queryParamString = split[1].split("&");

        for (String queryParam : queryParamString) {
            if (queryParam.contains("=")) {
                String[] keyValue = queryParam.split("=");
                String key = keyValue[0];
                String value = keyValue[1];
                queryParameters.put(key, value);
            }
        }

        return queryParameters;
    }

    private Map<String, Cookie> parseCookies(String str) {
        Map<String, Cookie> map = new HashMap<>();
        for (String t : str.split(";\\s*")) {
            String[] cookiePair = t.trim().split("=", 2);
            if (cookiePair.length < 2) continue;

            map.put(cookiePair[0].trim(), new Cookie(cookiePair[0].trim(), cookiePair[1].trim()));
        }

        return map;
    }

}
