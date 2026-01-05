package webserver.handling.static_route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.handling.ResponseEntity;
import webserver.http.data.HttpRequest;

import java.io.IOException;
import java.io.InputStream;

public enum StaticFileEnum {

    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css;charset=utf-8"),
    JS(".js", "application/javascript;charset=utf-8"),
    PNG(".png", "image/png"),
    ICO(".ico", "image/x-icon"),
    JPG(".jpg", "image/jpeg"),
    SVG(".svg", "image/svg+xml");

    private static final Logger logger = LoggerFactory.getLogger(StaticFileEnum.class);
    private final String ext;
    private final String contentType;

    StaticFileEnum(String ext, String contentType) {
        this.ext = ext;
        this.contentType = contentType;
    }

    public ResponseEntity<?> fetchStaticFileFromURI(HttpRequest httpRequest) {
        if (httpRequest.getRequestURI().endsWith(ext)) {
            return this.fetchStaticFile(httpRequest.getRequestURI().substring(1));
        }
        return ResponseEntity.notFound();
    }

    public ResponseEntity<?> fetchStaticFile(String resourcePath) {
        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("static/" + resourcePath)) {
            if (resourceStream == null) {
                return ResponseEntity.notFound();
            }

            return ResponseEntity.ok(resourceStream.readAllBytes(), contentType);

        } catch (IOException e) {
            return ResponseEntity.notFound();
        }
    }
}
