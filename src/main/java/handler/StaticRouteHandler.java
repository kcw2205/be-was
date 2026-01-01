package handler;

import webserver.data.HttpRequest;
import webserver.data.ResponseEntity;
import webserver.data.enums.StaticFileEnum;

public class StaticRouteHandler implements RequestHandler {

    @Override
    public ResponseEntity handleRequest(HttpRequest httpRequest) {
        if (httpRequest.getRequestURI().equals("/"))
            return handleRouteRequest(httpRequest.getRequestURI() + "index.html");
        return handleRouteRequest(httpRequest.getRequestURI() + "/index.html");
    }

    private ResponseEntity handleRouteRequest(String file) {
        ResponseEntity responseEntity = StaticFileEnum.HTML.fetchStaticFile(file.substring(1));
        System.out.println(responseEntity.getContentType());
        if (responseEntity == null) {
            throw new IllegalStateException("Static Resource not found.");
        }
        return responseEntity;
    }
}
