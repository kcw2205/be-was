package view.components;

import model.User;
import webserver.template.HtmlComponent;
import webserver.template.RawString;

import java.util.Map;

public class LoginUserGNB extends HtmlComponent {

    protected LoginUserGNB(User user) {
        super("index/user-menu", Map.of(
            "name", new RawString(user.getName())
        ));
    }
}
