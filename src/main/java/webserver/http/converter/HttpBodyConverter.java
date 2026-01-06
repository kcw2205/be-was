package webserver.http.converter;

import webserver.http.data.HttpRequestBody;

public interface HttpBodyConverter<T> {

    T convertFromBody(HttpRequestBody body);
}
