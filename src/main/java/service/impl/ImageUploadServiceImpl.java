package service.impl;

import exception.ServiceErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ImageUploadService;
import webserver.http.HttpException;
import webserver.http.enums.HttpContentType;
import webserver.resources.ImageUploadWorker;

import java.io.IOException;

public class ImageUploadServiceImpl implements ImageUploadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUploadServiceImpl.class);
    private final ImageUploadWorker imageUploadWorker;

    public ImageUploadServiceImpl(ImageUploadWorker imageUploadWorker) {
        this.imageUploadWorker = imageUploadWorker;
    }

    @Override
    public String uploadImage(byte[] file, HttpContentType contentType) throws HttpException {
        try {
            return this.imageUploadWorker.upload(file, contentType);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw ServiceErrorCode.IMAGE_UPLOAD_ERROR.toException();
        }
    }
}
