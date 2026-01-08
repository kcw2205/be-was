package webserver.handling.statics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.enums.HttpContentType;

public enum StaticFileEnum {

    HTML(".html", HttpContentType.HTML),
    CSS(".css", HttpContentType.CSS),
    JS(".js", HttpContentType.JS),
    PNG(".png", HttpContentType.PNG),
    ICO(".ico", HttpContentType.ICO),
    JPG(".jpg", HttpContentType.JPG),
    SVG(".svg", HttpContentType.SVG),
    ;

    private static final Logger logger = LoggerFactory.getLogger(StaticFileEnum.class);
    private final String ext;
    private final HttpContentType contentType;

    StaticFileEnum(String ext, HttpContentType contentType) {
        this.ext = ext;
        this.contentType = contentType;
    }

    public String getExt() {
        return ext;
    }

    public HttpContentType getContentType() {
        return contentType;
    }
}
