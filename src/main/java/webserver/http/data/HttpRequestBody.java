package webserver.http.data;

import webserver.http.converter.DataMapHttpBodyConverter;
import webserver.http.converter.HttpBodyConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.Map;

public record HttpRequestBody(byte[] data) {

    public <T> T getData(HttpBodyConverter<T> converter) {
        return converter.convertFromBody(this);
    }

    public <T extends Record> T mapToRecord(DataMapHttpBodyConverter converter, Class<T> clazz) {
        try {
            Map<String, String> map = converter.convertFromBody(this);

            RecordComponent[] components = clazz.getRecordComponents();
            Object[] args = new Object[components.length];
            Class<?>[] paramTypes = new Class<?>[components.length];

            for (int i = 0; i < components.length; ++i) {
                String name = components[i].getName();
                paramTypes[i] = components[i].getType();

                args[i] = map.get(name);
            }

            Constructor<T> constructor = clazz.getDeclaredConstructor(paramTypes);
            return constructor.newInstance(args);

        } catch (Exception e) {
            throw new RuntimeException("Failed to bind request body to data class", e);
        }
    }

    public static HttpRequestBody empty() {
        return new HttpRequestBody(new byte[0]);
    }
}
