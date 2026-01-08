package webserver.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

// .tpl 로 끝나는 별도의 html 프로세서
// 자식 Template 도 받을 수 있도록 하도록..
public class HtmlComponent<T> {
    private final Logger log = LoggerFactory.getLogger(HtmlComponent.class);
    private final T fields;
    private final String rawTemplate;

    private static final String START_DELIMITER = "{{";
    private static final String END_DELIMITER = "}}";
    private static final String TEMPLATE_FOLDER_PATH = "templates/";
    private static final String TEMPLATE_EXTENSION = ".html";

    public static <T> HtmlComponent<T> load(String templateFilePath, T field) {
        try {
            String resourcePath = TEMPLATE_FOLDER_PATH + templateFilePath + TEMPLATE_EXTENSION;
            URL resourceUrl = HtmlComponent.class.getClassLoader()
                .getResource(resourcePath);

            if (resourceUrl == null) {
                // 파일이 없으면 null을 반환하므로 예외 처리 필수
                throw new IllegalArgumentException("Template not found: " + resourcePath);
            }

            String str = Files.readString(Paths.get(resourceUrl.toURI()));
            return new HtmlComponent<>(str, field);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load html component: " + templateFilePath, e);
        }
    }

    private HtmlComponent(String rawTemplate, T fields) {
        this.rawTemplate = rawTemplate;
        this.fields = fields;
    }

    // 지정된 Property 가 없으면 IllegalArgumentException throw
    // Reflection API 를 활용하여 Datamap 이 아닌, 필드 출력
    //
    // 필드 별로 단순 Replace 시 O(필드 * template) 만큼의 시간이 걸리게 된다.
    //
    // 앞에서부터 읽어들이며 escape 문자를 만났을 때, }} 만큼 읽어들여서 fieldName 을 추출한다.
    // string builder 는 단순히
    public String toHtml() {
        StringBuilder sb = new StringBuilder();

        Map<String, Object> fieldMap = extractFields();

        int cursor = 0;

        while (cursor < rawTemplate.length()) {
            int start = rawTemplate.indexOf(START_DELIMITER, cursor);

            // 다 끝난 경우 이제 남은 rawTemplate만큼 붙이면 됨.
            if (start == -1) {
                sb.append(rawTemplate.substring(cursor));
                break;
            }

            // start 앞쪽까지 붙임.
            sb.append(rawTemplate, cursor, start);

            int end = rawTemplate.indexOf(END_DELIMITER, start);

            // start 는 있는데, end 는 없는 상황
            if (end == -1) {
                throw new IllegalArgumentException("Closing brackets are missing in template.");
            }

            String key = rawTemplate.substring(start + START_DELIMITER.length(), end).trim();
            Object value = fieldMap.get(key);

            if (value != null) {
                if (value instanceof HtmlComponent) {
                    // 값이 또 다른 Component라면 재귀 호출
                    sb.append(((HtmlComponent<?>) value).toHtml());
                } else {
                    // 일반 String이나 숫자라면 그대로 문자열 변환
                    // String 이 아닌 경우, 의도하지 않은 경우이므로 그냥 toString 사용했음.
                    sb.append(value.toString());
                }
            } else {
                // 데이터가 없으면 빈 문자열 혹은 디버그용 텍스트 처리
                // sb.append("");
                log.warn("Warn: No value found for key: {}", key);
            }

            // 커서 이동 (}} 뒤로)
            cursor = end + END_DELIMITER.length();
        }

        return sb.toString();
    }

    private Map<String, Object> extractFields() {
        Map<String, Object> map = new HashMap<>();
        if (this.fields == null) return map;

        for (Field field : fields.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(this.fields));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field: " + field.getName(), e);
            }
        }
        return map;
    }
}
