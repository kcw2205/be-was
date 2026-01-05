package webserver.http.data;


import webserver.http.enums.HttpStatusCode;

public interface ResponseEntity {

    public HttpStatusCode getHttpStatusCode();

    public byte[] getSerializedData();

    public String getContentType();
}
