package webserver.http.converter;


import webserver.http.data.HttpRequestBody;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UrlEncodedBodyConverter implements DataMapConverter {

    @Override
    public Map<String, String> convertFromBody(HttpRequestBody body) {
        try {
            String queryStr = new String(body.getData(), StandardCharsets.UTF_8);
            Map<String, String> formData = new HashMap<>();

            for (String query : queryStr.split("&")) {
                String[] splitPair = query.split("=");

                String key = URLDecoder.decode(splitPair[0], StandardCharsets.UTF_8);

                String value = "";
                if (splitPair.length > 1) {
                    value = URLDecoder.decode(splitPair[1], StandardCharsets.UTF_8);
                }

                formData.put(key, value);
            }

            return formData;
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Bad Request Body", e);
        }
    }
}
