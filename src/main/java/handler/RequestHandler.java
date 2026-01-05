package handler;

import webserver.http.data.HttpRequest;
import webserver.http.data.ResponseEntity;

public interface RequestHandler {

    public ResponseEntity handleRequest(HttpRequest httpRequest);
}
