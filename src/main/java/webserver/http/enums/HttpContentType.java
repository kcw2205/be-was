package webserver.http.enums;

// TODO: StaticFileEnum 과 통합하는 식으로 개선해야함
public enum HttpContentType {
    APPLICATION_JSON("application/json;charset=utf-8", ".json"),
    APPLICATION_XML("application/xml;charset=utf-8", ".xml"),
    URL_ENCODED("application/x-www-form-urlencoded;charset=utf-8", ""),
    TEXT_PLAIN("text/plain;charset=utf-8", ".txt"),
    HTML("text/html;charset=utf-8", ".html"),
    CSS("text/css;charset=utf-8", ".css"),
    JS("application/javascript;charset=utf-8", ".js"),
    PNG("image/png", ".png"),
    ICO("image/x-icon", ".ico"),
    JPG("image/jpeg", ".jpg"),
    SVG("image/svg+xml", ".svg"),
    OCTET_STREAM("application/octet-stream", ""); // VOID 반환의 경우 octet stream 으로 해도 된다.

    private final String name;
    private final String ext;

    HttpContentType(String name, String ext) {
        this.name = name;
        this.ext = ext;
    }

    public static HttpContentType fromMimeType(String mimeType) {
        if (mimeType == null || mimeType.isBlank()) {
            return OCTET_STREAM;
        }

        // 헤더에 charset이 포함된 경우를 대비해 세미콜론 앞부분만 추출
        String pureMimeType = mimeType.split(";")[0].trim().toLowerCase();

        for (HttpContentType type : values()) {
            // null 체크 (OCTET_STREAM 등 name이 null일 가능성 대비)
            if (type.name != null && type.name.toLowerCase().startsWith(pureMimeType)) {
                return type;
            }
        }

        return OCTET_STREAM;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String ext() {
        return this.ext;
    }
}
