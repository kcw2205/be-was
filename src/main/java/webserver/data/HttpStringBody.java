package webserver.data;

import java.io.UnsupportedEncodingException;

public class HttpStringBody implements HttpBody {
    private final String data;
    private final int byteLength;
    private final String contentType;

    public HttpStringBody(String data, String contentType) {
        this.data = data;
        this.byteLength = data.getBytes().length;
        this.contentType = contentType;
    }

    @Override
    public byte[] getContent() {
        try {
            return data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getContentAsString() {
        return this.data;
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
