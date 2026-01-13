package view.components;

import model.User;
import webserver.template.HtmlComponent;

import java.util.Map;

public class IndexComponent extends HtmlComponent {

    public IndexComponent(User user) {
        super("index", Map.of(
            "menuComponent", user != null ? new LoginUserGNB(user) : new GuestUserGNB()
        ));
    }
}
