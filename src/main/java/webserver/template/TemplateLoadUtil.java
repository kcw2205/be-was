package webserver.template;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

// .tpl 로 끝나는 별도의 html 프로세서
// 자식 Template 도 받을 수 있도록 하도록..
public class TemplateLoadUtil {
    private static final String TEMPLATE_FOLDER_PATH = "templates/";
    private static final String TEMPLATE_EXTENSION = ".html";

    public static String load(String templateFilePath) {
        try {
            String resourcePath = TEMPLATE_FOLDER_PATH + templateFilePath + TEMPLATE_EXTENSION;
            URL resourceUrl = TemplateLoadUtil.class.getClassLoader()
                .getResource(resourcePath);

            if (resourceUrl == null) {
                // 파일이 없으면 null을 반환하므로 예외 처리 필수
                throw new IllegalArgumentException("Template not found: " + resourcePath);
            }

            return Files.readString(Paths.get(resourceUrl.toURI()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load html component: " + templateFilePath, e);
        }

    }
}
