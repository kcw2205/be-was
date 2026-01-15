package service;

import webserver.http.HttpException;
import webserver.http.enums.HttpContentType;

public interface ImageUploadService {

    // TODO: 큰 이미지의 경우 한꺼번에 들 수 없다. 이때 어떻게 처리할 것인가?
    String uploadImage(byte[] file, HttpContentType contentType) throws HttpException;
}
