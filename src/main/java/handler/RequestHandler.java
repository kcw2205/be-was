package handler;

import webserver.data.HttpRequest;
import webserver.data.ResponseEntity;

public interface RequestHandler {

    public ResponseEntity handleRequest(HttpRequest httpRequest);
}
