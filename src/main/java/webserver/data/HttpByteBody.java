package webserver.data;

public class HttpByteBody implements HttpBody {
    private final byte[] data;
    private final int byteLength;
    private final String contentType;

    public HttpByteBody(byte[] data, String contentType) {
        this.data = data;
        this.byteLength = data.length;
        this.contentType = contentType;
    }

    @Override
    public byte[] getContent() {
        return data;
    }

    @Override
    public int getContentLength() {
        return this.byteLength;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }
}
