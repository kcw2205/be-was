package webserver.http.data;

import webserver.http.enums.HttpStatusCode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class HttpResponse {
    private final HttpStatusCode status;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpResponse(HttpStatusCode status, String httpVersion, Map<String, String> headers, byte[] body) {
        this.status = status;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.body = body;
    }

    public byte[] serialize() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String firstLine = this.httpVersion + " " + this.status.getCode() + " " + this.status.getStatusName() + "\r\n";
            baos.write(firstLine.getBytes());

            for (Map.Entry<String, String> entry : this.headers.entrySet()) {
                String line = entry.getKey() + ": " + entry.getValue() + "\r\n";

                baos.write(line.getBytes());
            }

            baos.write("\r\n".getBytes());

            if (this.body != null) {
                baos.write(this.body);
            }

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Response serialization failed", e);
        }
    }
}
