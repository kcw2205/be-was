package webserver.handling;


import webserver.http.enums.HttpStatusCode;

public class ResponseEntity<T> {
    private final byte[] data;
    private final HttpStatusCode httpStatusCode;
    // TODO: Content Type 부분 Enum 으로 개선 필요
    private final String contentType;

    private ResponseEntity(T data, HttpStatusCode status, String contentType) {
        this.httpStatusCode = status;
        this.contentType = contentType;

        if (data instanceof String) {
            this.data = ((String) data).getBytes(); // 텍스트는 인코딩해서 바이트로
        } else if (data instanceof byte[]) {
            this.data = (byte[]) data;
        } else {
            this.data = data.toString().getBytes();
        }
    }

    public static <T> ResponseEntity<T> ok(T data, String contentType) {
        return new ResponseEntity<>(data, HttpStatusCode.OK, contentType);
    }

    public static ResponseEntity<?> notFound() {
        return new ResponseEntity<>("", HttpStatusCode.NOT_FOUND, "text/plain");
    }

    public HttpStatusCode getHttpStatusCode() {
        return this.httpStatusCode;
    }

    public byte[] getSerializedBytes() {
        return this.data;
    }

    public String getContentType() {
        return this.contentType;
    }
}
