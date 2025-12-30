package webserver.data;


import webserver.data.enums.HttpStatusCode;

public interface ResponseEntity {

    public HttpStatusCode getHttpStatusCode();

    public byte[] getSerializedData();

    public String getContentType();
}
