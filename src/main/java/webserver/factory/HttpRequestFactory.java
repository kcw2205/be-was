package webserver.factory;

import webserver.data.enums.HttpRequestMethod;
import webserver.data.HttpBody;
import webserver.data.HttpRequest;
import webserver.data.HttpStringBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 바이트 배열로부터 파싱해서 이해하기 쉬운 HttpRequest 객체로 바꾸어주는 역할을 하는 클래스
 *
 * 후에 멀티파트같은 부분도 파싱해서 병합해주는 것도 이곳에서 할 수 있도록 하면 좋을 듯 하다.
 */
public class HttpRequestFactory {

    public HttpRequest parseRequestFromStream(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String[] requestMethodAndURI = parseFirstLine(bufferedReader);
        Map<String, String> headers = parseHeader(bufferedReader);

        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        String contentType = headers.get("Content-Type");

        // TODO: null 로 넣는게 좋은 방법일까? 다만 굳이 생성자 오버로딩을 해야할까?
        return new HttpRequest(
                HttpRequestMethod.valueOf(requestMethodAndURI[0]),
                requestMethodAndURI[1],
                requestMethodAndURI[2],
                headers,
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
}
