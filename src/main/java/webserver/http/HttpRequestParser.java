package webserver.http;

import webserver.http.data.Cookie;
import webserver.http.data.HttpRequest;
import webserver.http.data.HttpRequestBody;
import webserver.http.enums.HttpContentType;
import webserver.http.enums.HttpHeaderKey;
import webserver.http.enums.HttpRequestMethod;

import java.io.IOException;
import java.io.InputStream;
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
        // 1. 첫 줄 파싱 (readLine 직접 구현체 사용)
        String firstLine = readLine(inputStream);
        if (firstLine == null) throw new IOException("Invalid HTTP Request");
        String[] requestMethodAndURI = firstLine.split(" ");

        // 2. 헤더 파싱
        Map<String, String> headers = parseHeader(inputStream);

        // 3. 메타데이터 추출
        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        HttpContentType contentType = HttpContentType.fromMimeType(headers.get("content-type"));
        String cookieStr = headers.getOrDefault(HttpHeaderKey.COOKIE.toString().toLowerCase(), "");
        String[] uriParts = requestMethodAndURI[1].split("\\?");

        // 4. 바디 파싱 (바이트 직접 읽기)
        HttpRequestBody body = (contentLength > 0)
            ? parseBody(contentLength, inputStream)
            : HttpRequestBody.empty();

        return new HttpRequest(
            HttpRequestMethod.valueOf(requestMethodAndURI[0]),
            uriParts[0],
            requestMethodAndURI[2],
            contentType,
            headers,
            parseCookies(cookieStr),
            parseQueryParameters(URLDecoder.decode(requestMethodAndURI[1], "UTF-8")),
            body
        );
    }

    private Map<String, String> parseHeader(InputStream in) throws IOException {
        Map<String, String> map = new HashMap<>();
        String line;
        while ((line = readLine(in)) != null && !line.isEmpty()) {
            String[] tokens = line.split(":", 2);
            if (tokens.length == 2) {
                map.put(tokens[0].toLowerCase().trim(), tokens[1].trim());
            }
        }
        return map;
    }

    private HttpRequestBody parseBody(int contentLength, InputStream in) throws IOException {
        byte[] bytes = new byte[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            int read = in.read(bytes, totalRead, contentLength - totalRead);
            if (read == -1) break;
            totalRead += read;
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
                String[] keyValue = queryParam.split("=", 2);
                String key = keyValue[0];

                String value = "";

                if (keyValue.length > 1) {
                    value = keyValue[1];
                }

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


    private String readLine(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        int b;
        while ((b = in.read()) != -1) {
            if (b == '\r') {
                int next = in.read();
                if (next == '\n') break;
                sb.append((char) b).append((char) next);
            } else {
                sb.append((char) b);
            }
        }
        if (b == -1 && sb.isEmpty()) return null;
        return sb.toString();
    }
}
