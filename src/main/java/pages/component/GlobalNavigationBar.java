package pages.component;

import model.User;
import webserver.template.HtmlComponent;
import webserver.template.RawString;

public class GlobalNavigationBar {

    private final HtmlComponent component;

    public GlobalNavigationBar(User user) {
        this.component = user != null ?
            new HtmlComponent("gnb/user-menu") :
            new HtmlComponent("gnb/guest-menu");

        if (user != null) {
            this.component.setField("name", new RawString(user.getName()));
        }
    }

    public HtmlComponent getComponent() {
        return component;
    }
}
