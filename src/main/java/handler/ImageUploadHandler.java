package handler;

import dto.output.UploadFileOutput;
import service.ImageUploadService;
import webserver.handling.ResponseEntity;
import webserver.http.HttpException;
import webserver.http.data.HttpRequest;
import webserver.http.enums.HttpContentType;

public class ImageUploadHandler {
    private final ImageUploadService imageUploadService;

    public ImageUploadHandler(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    public ResponseEntity<UploadFileOutput> uploadFile(HttpRequest httpRequest) throws HttpException {
        byte[] file = httpRequest.body().data();
        String path = imageUploadService.uploadImage(file, httpRequest.httpContentType());

        return ResponseEntity.ok(new UploadFileOutput(path), HttpContentType.APPLICATION_JSON);
    }
}
