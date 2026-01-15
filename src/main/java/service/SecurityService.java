package service;

public class SecurityService {

    public String escapeXss(String input) {
        if (input == null) return null;
        return input.replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll("\"", "&quot;")
            .replaceAll("'", "&#x27;");
    }
}
