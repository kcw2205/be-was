package handler;

import webserver.data.HttpRequest;
import webserver.data.ResponseEntity;
import webserver.data.enums.StaticFileEnum;

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
