package webserver.handling.statics;

import webserver.handling.ResponseEntity;
import webserver.http.HttpException;
import webserver.http.data.HttpRequest;
import webserver.http.enums.HttpContentType;
import webserver.http.enums.HttpStatusCode;

public class StaticHandler {

    private static final String INDEX_HTML = "/index.html";
    private static final String NOT_FOUND_PAGE = "/notfound.html";
    private final StaticFileResolver staticFileResolver;

    public StaticHandler(StaticFileResolver staticFileResolver) {
        this.staticFileResolver = staticFileResolver;
    }

    public ResponseEntity<?> handleStaticRouteRequest(HttpRequest httpRequest) throws HttpException {
        // 먼저, 정적 파일 요청인지 확인
        ResponseEntity<byte[]> staticFileResponse = getStaticFileResponse(httpRequest);

        if (staticFileResponse != null) {
            return staticFileResponse;
        }

        // 그 이후에 정적 경로 라우팅이라면, 웹 디렉토리 인덱스 html 파일 반환
        // 그럼에도 아니면 기본 404 에러를 리턴 하도록 수행
        return handleWebDirectoryIndexRequest(httpRequest.requestURI());
    }

    private ResponseEntity<byte[]> getStaticFileResponse(HttpRequest httpRequest) {

        for (StaticFileEnum e : StaticFileEnum.values()) {
            if (!httpRequest.requestURI().endsWith(e.getExt())) continue;

            byte[] bytes = staticFileResolver.fetchStaticFile(httpRequest.requestURI()).orElse(null);

            if (bytes != null) {
                return ResponseEntity.ok(bytes, e.getContentType());
            }
        }

        return null;
    }

    private ResponseEntity<?> handleWebDirectoryIndexRequest(String uri) throws HttpException {
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        uri += INDEX_HTML;

        byte[] payload = staticFileResolver.fetchStaticFile(uri).orElse(null);

        if (payload == null) {
            return handleNotFoundPage();
        }

        return ResponseEntity.ok(payload, HttpContentType.HTML);
    }

    public ResponseEntity<String> handleNotFoundPage() throws HttpException {
        HttpException e = new HttpException(HttpStatusCode.INTERNAL_SERVER_ERROR, HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusName());

        byte[] b = staticFileResolver.fetchStaticFile(NOT_FOUND_PAGE).orElseThrow(() -> e);

        return ResponseEntity.create(b, HttpStatusCode.NOT_FOUND, HttpContentType.HTML);
    }
}
