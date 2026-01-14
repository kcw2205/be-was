package webserver.resources;

import exception.ServiceErrorCode;
import webserver.http.HttpException;
import webserver.http.enums.HttpContentType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class ImageUploadWorker {
    private final String uploadBaseDir = ResourcePath.EXTERNAL_STORAGE.getPath();

    public ImageUploadWorker() {
        this.init();
    }

    private void init() {
        File dir = new File(uploadBaseDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }
    }


    /**
     * TODO: 메모리 낭비가 심하다. 소켓 단에서 버퍼로 읽고 써야한다.
     *
     * @param files
     * @param contentType
     * @return
     * @throws IOException
     * @throws HttpException
     */
    public String upload(byte[] files, HttpContentType contentType) throws IOException, HttpException {

        String ext = getExtension(contentType);
        String fileName = UUID.randomUUID() + ext;

        Path targetPath = Paths.get(uploadBaseDir, fileName);

        Files.write(targetPath, files);

        return fileName;
    }

    private String getExtension(HttpContentType contentType) throws HttpException {
        return switch (contentType) {
            case PNG, JPG -> contentType.ext();
            default -> throw ServiceErrorCode.BAD_REQUEST_FORMAT.toException();
        };
    }
}
