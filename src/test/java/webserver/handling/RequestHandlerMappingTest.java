package webserver.handling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.handling.statics.StaticFileResolver;
import webserver.handling.statics.StaticHandler;
import webserver.http.data.HttpRequest;
import webserver.http.data.HttpRequestBody;
import webserver.http.enums.HttpRequestMethod;
import webserver.http.enums.HttpStatusCode;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestHandlerMappingTest {

    private RequestHandlerMapping mapping;
    private StubStaticHandler stubStaticHandler;

    // StaticHandler를 상속받아 호출 여부를 추적하는 스텁
    static class StubStaticHandler extends StaticHandler {
        boolean called = false;

        public StubStaticHandler() {
            super(new StaticFileResolver()); // 더미 리졸버
        }

        @Override
        public ResponseEntity<?> handleStaticRouteRequest(HttpRequest httpRequest) {
            this.called = true;
            return ResponseEntity.simple(HttpStatusCode.NOT_FOUND); // 아무거나 반환
        }
    }

    @BeforeEach
    void setUp() {
        stubStaticHandler = new StubStaticHandler();
        mapping = new RequestHandlerMapping(stubStaticHandler);
    }

    @Test
    @DisplayName("등록된 경로와 메서드로 요청 시 해당 핸들러를 반환해야 한다")
    void returnRegisteredHandler() {
        // given
        String path = "/api/test";
        HttpRequestMethod method = HttpRequestMethod.GET;

        // 핸들러 등록 (호출되었는지 확인하기 위해 플래그 사용)
        final boolean[] handlerCalled = {false};
        RequestHandler handler = request -> {
            handlerCalled[0] = true;
            return ResponseEntity.ok(new byte[0], null);
        };

        mapping.registerRequestHandler(path, method, handler);

        // 가짜 요청 생성
        HttpRequest request = new HttpRequest(
            method, path, "HTTP/1.1",
            new HashMap<>(), new HashMap<>(), new HashMap<>(),
            new HttpRequestBody(new byte[0])
        );

        // when
        RequestHandler retrievedHandler = mapping.getRequestHandler(request);
        retrievedHandler.handle(request);

        // then
        assertTrue(handlerCalled[0], "등록된 핸들러가 실행되어야 함");
        assertFalse(stubStaticHandler.called, "정적 핸들러는 실행되지 않아야 함");
    }

    @Test
    @DisplayName("등록되지 않은 경로로 요청 시 정적 핸들러(Fallback)를 반환해야 한다")
    void returnStaticFallbackHandler() {
        // given
        String path = "/unknown/path";
        HttpRequestMethod method = HttpRequestMethod.GET;

        // 가짜 요청 생성
        HttpRequest request = new HttpRequest(
            method, path, "HTTP/1.1",
            new HashMap<>(), new HashMap<>(), new HashMap<>(),
            new HttpRequestBody(new byte[0])
        );

        // when
        RequestHandler retrievedHandler = mapping.getRequestHandler(request);
        retrievedHandler.handle(request);

        // then
        assertTrue(stubStaticHandler.called, "정적 핸들러가 실행되어야 함");
    }

    @Test
    @DisplayName("경로는 같지만 메서드가 다른 경우 등록된 핸들러가 실행되지 않아야 한다 (현재는 Fallback)")
    void differentiateMethod() {
        // given
        String path = "/api/resource";

        // GET만 등록
        mapping.registerRequestHandler(path, HttpRequestMethod.GET, request -> ResponseEntity.ok(new byte[0], null));

        // POST로 요청
        HttpRequest request = new HttpRequest(
            HttpRequestMethod.POST, path, "HTTP/1.1",
            new HashMap<>(), new HashMap<>(), new HashMap<>(),
            new HttpRequestBody(new byte[0])
        );

        // when
        RequestHandler retrievedHandler = mapping.getRequestHandler(request);
        retrievedHandler.handle(request);

        // then
        assertTrue(stubStaticHandler.called, "메서드가 다르면 정적 핸들러(Fallback)로 가야 함");
    }
}
