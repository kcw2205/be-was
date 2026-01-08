package webserver.handling.statics;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class StaticFileResolver {

    public Optional<byte[]> fetchStaticFile(String resourcePath) {
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
