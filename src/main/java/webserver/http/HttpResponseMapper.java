package webserver.http;

import java.lang.reflect.RecordComponent;
import java.util.List;

// TODO: static 함수 대신 좋은 방법 없을까?
// TODO: xml 로도 반환할 수도 있을 테고, json으로도 반환할 수 있을 것이다. 컨버터나 전략을 분리할 수 있도록?
public class HttpResponseMapper {

    public static byte[] mapToByteArray(Object obj) {
        if (obj == null) return new byte[0];

        if (obj instanceof byte[]) return (byte[]) obj;

        if (obj instanceof String) return ((String) obj).getBytes();

        // List인 경우 처리 (재귀적으로 각 요소를 변환)
        if (obj instanceof List<?> list) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < list.size(); i++) {
                // 리스트 내부 요소에 대해 다시 mapToByteArray를 호출하여 바이트를 얻고 문자열로 변환
                sb.append(new String(mapToByteArray(list.get(i))));
                if (i < list.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("]");
            return sb.toString().getBytes();
        }

        if (obj instanceof Record) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");

            RecordComponent[] components = obj.getClass().getRecordComponents();
            for (int i = 0; i < components.length; i++) {
                RecordComponent comp = components[i];
                try {
                    String name = comp.getName();
                    Object value = comp.getAccessor().invoke(obj);

                    sb.append("\"").append(name).append("\":");

                    if (value instanceof Number || value instanceof Boolean) {
                        sb.append(value);
                    } else if (value == null) {
                        sb.append("null");
                    } else {
                        sb.append("\"").append(value.toString()).append("\"");
                    }

                    if (i < components.length - 1) {
                        sb.append(",");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            sb.append("}");
            return sb.toString().getBytes();
        }

        return obj.toString().getBytes();
    }
}
