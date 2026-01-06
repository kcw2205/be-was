package webserver.http.data;

import webserver.http.converter.DataMapConverter;
import webserver.http.converter.HttpBodyConverter;

import java.lang.reflect.Field;
import java.util.Map;

public class HttpRequestBody {
    private final byte[] data;

    public HttpRequestBody(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public <T> T getData(HttpBodyConverter<T> converter) {
        return converter.convertFromBody(this);
    }

    public <T> T getDataAs(DataMapConverter converter, Class<T> clazz) {
        try {
            Map<String, String> map = converter.convertFromBody(this);

            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                String value = map.get(fieldName);

                if (value != null) {
                    field.set(instance, value);
                }
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to bind request body to Data class", e);
        }
    }

    public static HttpRequestBody empty() {
        return new HttpRequestBody(new byte[0]);
    }
}
