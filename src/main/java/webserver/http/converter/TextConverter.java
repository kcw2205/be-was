package webserver.http.converter;

import webserver.http.data.HttpRequestBody;

public class TextConverter implements HttpBodyConverter<String> {
    @Override
    public String convertFromBody(HttpRequestBody body) {
        return new String(body.data());
    }
}
