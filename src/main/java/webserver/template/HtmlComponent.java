package webserver.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

// .tpl 로 끝나는 별도의 html 프로세서
// 자식 Template 도 받을 수 있도록 하도록..
public class HtmlComponent implements Renderable {
    private static final String START_DELIMITER = "{{";
    private static final String END_DELIMITER = "}}";
    private final Logger LOGGER = LoggerFactory.getLogger(HtmlComponent.class);
    private final String template;
    private Map<String, Renderable> fieldMap;

    public HtmlComponent(String templateFilePath, Map<String, Renderable> fieldMap) {
        this.template = TemplateLoadUtil.load(templateFilePath);
        this.fieldMap = fieldMap == null ? new HashMap<>() : fieldMap;
    }

    public void refreshFieldMap(Map<String, Renderable> fieldMap) {
        this.fieldMap = new HashMap<>();
    }

    @Override
    public String render() {
        StringBuilder sb = new StringBuilder();

        int cursor = 0;

        while (cursor < template.length()) {
            int start = template.indexOf(START_DELIMITER, cursor);

            // 다 끝난 경우 이제 남은 rawTemplate만큼 붙이면 됨.
            if (start == -1) {
                sb.append(template.substring(cursor));
                break;
            }

            // start 앞쪽까지 붙임.
            sb.append(template, cursor, start);

            int end = template.indexOf(END_DELIMITER, start);

            // start 는 있는데, end 는 없는 상황
            if (end == -1) {
                throw new IllegalArgumentException("Closing brackets are missing in template.");
            }

            String key = template.substring(start + START_DELIMITER.length(), end).trim();
            Renderable value = fieldMap.get(key);

            if (value != null) {
                if (value instanceof HtmlComponent) {
                    // 값이 또 다른 Component라면 재귀 호출
                    sb.append(value.render());
                } else {
                    // 일반 String이나 숫자라면 그대로 문자열 변환
                    // String 이 아닌 경우, 의도하지 않은 경우이므로 그냥 toString 사용했음.
                    sb.append(value);
                }
            } else {
                // 데이터가 없으면 빈 문자열 혹은 디버그용 텍스트 처리
                // sb.append("");
                LOGGER.warn("Warn: No value found for key: {}", key);
            }

            // 커서 이동 (}} 뒤로)
            cursor = end + END_DELIMITER.length();
        }

        return sb.toString();
    }
}
