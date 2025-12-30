package webserver.data;

import webserver.data.enums.HttpStatusCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpResponse {
    private final HttpStatusCode status;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final HttpBody body;

    public HttpResponse(HttpStatusCode status, String httpVersion, Map<String, String> headers, HttpBody body) {
        this.status = status;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.body = body;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpBody getBody() {
        return body;
    }

    public byte[] serialize() {
        List<Byte> byteList = new ArrayList<>();

        String firstLine = this.httpVersion + " " + this.status.getCode() + " " + this.status.getStatusName() + "\r\n";

        for (byte b : firstLine.getBytes()) {
            byteList.add(b);
        }

        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            String line = entry.getKey() + ": " + entry.getValue() + "\r\n";

            for (byte b : line.getBytes()) {
                byteList.add(b);
            }
        }


        String contentDataString = "Content-Type: " + body.getContentType() + "\r\nContent-Length: " + body.getContentLength() + "\r\n";

        for (byte b : contentDataString.getBytes()) {
            byteList.add(b);
        }

        byteList.add((byte) '\r');
        byteList.add((byte) '\n');

        for (byte b : this.body.getContent()) {
            byteList.add(b);
        }

        byte[] byteArray = new byte[byteList.size()];

        for (int i=0; i<byteList.size(); ++i) {
            byteArray[i] = byteList.get(i);
        }

        return byteArray;
    }
}
