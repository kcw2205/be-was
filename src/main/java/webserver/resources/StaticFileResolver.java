package webserver.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

public class StaticFileResolver {

    public Optional<byte[]> fetchStaticFile(String resourcePath) {
        try {
            File externalFile = new File(ResourcePath.EXTERNAL_STORAGE.getPath(), resourcePath);
            if (externalFile.exists() && externalFile.isFile()) {
                return Optional.of(Files.readAllBytes(externalFile.toPath()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("static/" + resourcePath)) {
            if (resourceStream == null) {
                return Optional.empty();
            }

            return Optional.of(resourceStream.readAllBytes());

        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
