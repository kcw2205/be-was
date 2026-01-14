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
    private static final String RENDER_INDEX_KEY = "index";
    private final Logger LOGGER = LoggerFactory.getLogger(HtmlComponent.class);
    private final String template;
    private final Map<String, Renderable> fieldMap;

    public HtmlComponent(String templateFilePath) {
        this.template = TemplateLoadUtil.load(templateFilePath);
        this.fieldMap = new HashMap<>();
    }

    public void setField(String fieldName, Renderable field) {
        this.fieldMap.put(fieldName, field);
    }

    // Package-Private 로 만들어서 외부에서는 Private 로 사용
    String renderByIndex(int index) {
        this.fieldMap.put(RENDER_INDEX_KEY, new RawString(String.valueOf(index)));
        return render();
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
                sb.append(value.render());
            } else {
                LOGGER.warn("Warn: No value found for key: {}", key);
            }

            // 커서 이동 (}} 뒤로)
            cursor = end + END_DELIMITER.length();
        }

        return sb.toString();
    }
}
