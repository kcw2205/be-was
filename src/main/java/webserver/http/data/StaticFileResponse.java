package webserver.http.data;

import webserver.http.enums.HttpStatusCode;

public class StaticFileResponse implements ResponseEntity {
    private final HttpStatusCode httpStatusCode;
    private final byte[] data;
    private final String contentType;

    public StaticFileResponse(HttpStatusCode httpStatusCode, byte[] data, String contentType) {
        this.httpStatusCode = httpStatusCode;
        this.data = data;
        this.contentType = contentType;
    }


    @Override
    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    @Override
    public byte[] getSerializedData() {
        return data;
    }

    @Override
    public String getContentType() {
        return contentType;
    }
}
