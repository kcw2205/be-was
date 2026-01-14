package view;

import model.User;
import view.components.IndexComponent;
import webserver.handling.ResponseEntity;
import webserver.http.data.Cookie;
import webserver.http.data.HttpRequest;
import webserver.http.enums.HttpContentType;
import webserver.session.SessionManager;

public class ViewHandler {

    private final SessionManager sessionManager;

    public ViewHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public ResponseEntity<byte[]> indexPage(HttpRequest httpRequest) {
        Cookie cookie = httpRequest.getCookieByName(SessionManager.SESSION_ID).orElse(null);

        if (cookie == null) {
            return ResponseEntity.ok(new IndexComponent(null).render().getBytes(), HttpContentType.HTML);
        }

        User user = (User) sessionManager.findById(cookie.getValue()).orElse(null);

        if (user == null) {
            return ResponseEntity.ok(new IndexComponent(null).render().getBytes(), HttpContentType.HTML);
        }

        return ResponseEntity.ok(new IndexComponent(user).render().getBytes(), HttpContentType.HTML);
    }
}
