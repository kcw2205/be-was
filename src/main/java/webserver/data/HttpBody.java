package webserver.data;

public interface HttpBody {

    byte[] getContent();

    int getContentLength();

    String getContentType();
}
