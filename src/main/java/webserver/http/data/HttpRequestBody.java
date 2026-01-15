package webserver.http.data;

import webserver.http.HttpException;
import webserver.http.converter.DataMapHttpBodyConverter;
import webserver.http.converter.HttpBodyConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.Map;

public record HttpRequestBody(byte[] data) {

    public <T> T getData(HttpBodyConverter<T> converter) {
        return converter.convertFromBody(this);
    }

    public <T extends Record> T mapToRecord(DataMapHttpBodyConverter converter, Class<T> clazz) throws HttpException {
        try {
            Map<String, String> map = converter.convertFromBody(this);

            RecordComponent[] components = clazz.getRecordComponents();
            Object[] args = new Object[components.length];
            Class<?>[] paramTypes = new Class<?>[components.length];

            for (int i = 0; i < components.length; ++i) {
                String name = components[i].getName();
                Class<?> type = components[i].getType();
                paramTypes[i] = components[i].getType();

                args[i] = convertValue(map.get(name), type);
            }

            Constructor<T> constructor = clazz.getDeclaredConstructor(paramTypes);
            return constructor.newInstance(args);

        } catch (Exception e) {
            throw new RuntimeException("요청 형식이 올바르지 않습니다. 입력 파라미터를 확인하세요.", e);
        }
    }

    private Object convertValue(String value, Class<?> type) {
        if (value == null || value.isBlank()) {
            if (type == long.class || type == int.class) return 0;
            return null;
        }

        if (type == String.class) return value;
        if (type == long.class || type == Long.class) return Long.parseLong(value);
        if (type == int.class || type == Integer.class) return Integer.parseInt(value);
        if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(value);

        return value;
    }
}
