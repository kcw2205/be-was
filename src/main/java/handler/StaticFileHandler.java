package handler;

import webserver.http.data.HttpRequest;
import webserver.http.data.ResponseEntity;
import webserver.http.enums.StaticFileEnum;

public class StaticFileHandler implements RequestHandler {

    @Override
    public ResponseEntity handleRequest(HttpRequest httpRequest) {
        ResponseEntity responseEntity = null;

        for (StaticFileEnum e : StaticFileEnum.values()) {
            responseEntity = e.fetchStaticFileFromURI(httpRequest);
            if (responseEntity != null) return responseEntity;
        }

        return null;
    }

}
