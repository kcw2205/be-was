package webserver.http.converter;


import webserver.http.data.HttpRequestBody;

import java.util.HashMap;
import java.util.Map;

public class FormDataConverter implements DataMapConverter {

    @Override
    public Map<String, String> convertFromBody(HttpRequestBody body) {
        try {
            String queryStr = new String(body.getData());
            Map<String, String> formData = new HashMap<>();

            for (String query : queryStr.split("&")) {
                String[] pairs = query.split("=");
                formData.put(pairs[0], pairs[1]);
            }

            return formData;
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Bad Request Body", e);
        }
    }
}
