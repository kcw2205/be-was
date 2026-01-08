package handler;

import model.User;
import webserver.handling.ResponseEntity;
import webserver.handling.statics.StaticFileResolver;
import webserver.http.data.Cookie;
import webserver.http.data.HttpRequest;
import webserver.http.enums.HttpContentType;
import webserver.http.enums.HttpStatusCode;
import webserver.session.SessionManager;
import webserver.template.HtmlComponent;

import java.nio.charset.StandardCharsets;

public class IndexHandler {

    static class MenuProp {
        private String name;

        public MenuProp(String name) {
            this.name = name;
        }
    }

    static class IndexProp {
        private HtmlComponent<?> menuComponent;

        public IndexProp(HtmlComponent<?> menuComponent) {
            this.menuComponent = menuComponent;
        }
    }

    private final StaticFileResolver staticFileResolver;
    private final SessionManager sessionManager;

    public IndexHandler(StaticFileResolver staticFileResolver, SessionManager sessionManager) {
        this.staticFileResolver = staticFileResolver;
        this.sessionManager = sessionManager;
    }

    public ResponseEntity<byte[]> index(HttpRequest request) {
        Cookie cookie = request.getCookieByName(SessionManager.SESSION_ID).orElse(null);

        HtmlComponent<?> menuComponent = null;

        if (cookie == null || sessionManager.findById(cookie.getValue()).isEmpty()) {
            menuComponent = HtmlComponent.load("index/guest-menu", null);
        } else {
            User user = (User) sessionManager
                .findById(cookie.getValue())
                .orElseThrow(IllegalStateException::new);


            menuComponent = HtmlComponent.load("index/user-menu", new MenuProp(user.getName()));
        }

        HtmlComponent<?> indexPage = HtmlComponent.load("index", new IndexProp(menuComponent));

        return ResponseEntity.builder(indexPage.toHtml().getBytes(StandardCharsets.UTF_8), HttpStatusCode.OK, HttpContentType.HTML);
    }
}
