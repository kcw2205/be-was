package webserver.handling.routes;

import webserver.handling.ResponseEntity;
import webserver.http.data.HttpRequest;
import webserver.http.enums.HttpContentType;
import webserver.http.enums.HttpRequestMethod;
import webserver.http.enums.HttpStatusCode;

public class StaticHandler {

    private final StaticFileResolver staticFileResolver;

    public StaticHandler(StaticFileResolver staticFileResolver) {
        this.staticFileResolver = staticFileResolver;
    }

    public ResponseEntity<?> handleStaticRouteRequest(HttpRequest httpRequest) {
        if (httpRequest.getHttpMethod() != HttpRequestMethod.GET) {
            return ResponseEntity.simple(HttpStatusCode.NOT_FOUND);
        }

        // 먼저, 정적 파일 요청인지 확인
        ResponseEntity<?> staticFileResponse = handleStaticFileRequest(httpRequest);

        if (staticFileResponse.getHttpStatusCode() != HttpStatusCode.NOT_FOUND) {
            return staticFileResponse;
        }

        // 그 이후에 정적 경로 라우팅이라면, 웹 디렉토리 인덱스 html 파일 반환
        // 그럼에도 아니면 404 에러)를 리턴 하도록 수행
        return handleRouteRequest(httpRequest.getRequestURI());
    }

    private ResponseEntity<?> handleStaticFileRequest(HttpRequest httpRequest) {

        for (StaticFileEnum e : StaticFileEnum.values()) {
            if (!httpRequest.getRequestURI().endsWith(e.getExt())) continue;

            byte[] t = staticFileResolver.fetchStaticFile(httpRequest.getRequestURI()).orElse(null);

            if (t != null) {
                return ResponseEntity.ok(t, e.getContentType());
            }
        }

        return ResponseEntity.simple(HttpStatusCode.NOT_FOUND);
    }

    private ResponseEntity<?> handleRouteRequest(String uri) {
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        uri += "/index.html";

        byte[] payload = staticFileResolver.fetchStaticFile(uri).orElse(null);

        if (payload == null) {
            return ResponseEntity.simple(HttpStatusCode.NOT_FOUND);
        }

        return ResponseEntity.ok(payload, HttpContentType.HTML);
    }
}
