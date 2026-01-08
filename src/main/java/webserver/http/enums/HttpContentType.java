package webserver.http.enums;

// TODO: StaticFileEnum 과 통합하는 식으로 개선해야함
public enum HttpContentType {
    APPLICATION_JSON("application/json;charset=utf-8"),
    APPLICATION_XML("application/xml;charset=utf-8"),
    URL_ENCODED("application/x-www-form-urlencoded;charset=utf-8"),
    TEXT_PLAIN("text/plain;charset=utf-8"),
    HTML("text/html;charset=utf-8"),
    CSS("text/css;charset=utf-8"),
    JS("application/javascript;charset=utf-8"),
    PNG("image/png"),
    ICO("image/x-icon"),
    JPG("image/jpeg"),
    SVG("image/svg+xml");

    private final String name;

    HttpContentType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
