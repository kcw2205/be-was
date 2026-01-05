package webserver.http;

import webserver.http.data.HttpBody;
import webserver.http.data.HttpRequest;
import webserver.http.data.HttpStringBody;
import webserver.http.enums.HttpRequestMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 바이트 배열로부터 파싱해서 이해하기 쉬운 HttpRequest 객체로 바꾸어주는 역할을 하는 클래스
 * <p>
 * 후에 멀티파트같은 부분도 파싱해서 병합해주는 것도 이곳에서 할 수 있도록 하면 좋을 듯 하다.
 */
public class HttpRequestParser {

    public HttpRequest parseRequestFromStream(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String[] requestMethodAndURI = parseFirstLine(bufferedReader);
        Map<String, String> headers = parseHeader(bufferedReader);

        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        String contentType = headers.get("Content-Type");

        String[] requestUri = requestMethodAndURI[1].split("\\?");

        // TODO: null 로 넣는게 좋은 방법일까? 다만 굳이 생성자 오버로딩을 해야할까?
        return new HttpRequest(
            HttpRequestMethod.valueOf(requestMethodAndURI[0]),
            requestUri[0],
            requestMethodAndURI[2],
            headers,
            parseQueryParameters(URLDecoder.decode(requestMethodAndURI[1], "UTF-8")),
            contentLength != 0 ? parseBody(contentType, contentLength, inputStream) : null
        );
    }

    private String[] parseFirstLine(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();

        String[] requestMethodAndURI = line.split("\\s");

        if (requestMethodAndURI.length != 3) {
            throw new IllegalArgumentException("Invalid Request Format : First line does not match the format");
        }

        return requestMethodAndURI;
    }

    private Map<String, String> parseHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> map = new HashMap<>();

        String line = bufferedReader.readLine();

        while (line != null) {
            line = bufferedReader.readLine();
            if (line.isEmpty()) { // 빈 라인을 찾았을 경우 Header 의 끝이다.
                return map;
            }

            String[] tokens = line.split(":\\s+");

            if (tokens.length != 2) {
                throw new IllegalArgumentException("Invalid Request Format.");
            }

            map.put(tokens[0], tokens[1]);
        }

        throw new IllegalArgumentException("Invalid Request Format.");
    }

    private HttpBody parseBody(String contentType, int contentLength, InputStream inputStream) throws IOException {
        byte[] data = inputStream.readNBytes(contentLength);

        // TODO: Content Type 에 따라 다양한 Body를 다양한 방식으로 Parse 할 수도 있게 하면 좋음. 현재로써는 단순한 String 요청만 지원하도록 작성
        return new HttpStringBody(Arrays.toString(data), contentType);
    }

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
}
