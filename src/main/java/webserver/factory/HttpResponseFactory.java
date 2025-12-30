package webserver.factory;

import webserver.data.enums.HttpStatusCode;
import webserver.data.HttpByteBody;
import webserver.data.HttpResponse;
import webserver.data.HttpStringBody;

import java.util.HashMap;

/**
 * 각종 DTO 로부터 데이터들을 Map으로 변환해 하나의 Http 응답 객체로 만들어주는 책임을 지님.
 *
 * 바이트 배열로 직렬화해서 소켓에 넘겨주는 일은 하면 안됨.
 */
public class HttpResponseFactory {

    // TODO: 이미지 등 다양한 타입의 경우 어떻게 지원할지 고민해보기
    public HttpResponse createResponse(String httpVersion, HttpStatusCode httpStatusCode, String data, String contentType) {
        var headers = new HashMap<String, String>();

        return new HttpResponse(httpStatusCode, httpVersion, headers, new HttpStringBody(data, contentType));
    }

    public HttpResponse createResponse(String httpVersion, HttpStatusCode httpStatusCode, byte[] data, String contentType) {
        var headers = new HashMap<String, String>();

        return new HttpResponse(httpStatusCode, httpVersion, headers, new HttpByteBody(data, contentType));
    }

    // TODO: mapObject -> JSON -> get Content Length
    public HttpResponse createResponse(String httpVersion, HttpStatusCode httpStatusCode) {
        var headers = new HashMap<String, String>();

        return new HttpResponse(httpStatusCode, httpVersion, headers, null);
    }
}
