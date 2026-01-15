package webserver.handling;

import webserver.http.HttpException;
import webserver.http.data.HttpRequest;

@FunctionalInterface
public interface RequestHandler {

    ResponseEntity<?> handle(HttpRequest httpRequest) throws HttpException;
}
