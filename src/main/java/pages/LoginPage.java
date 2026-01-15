package pages;

import webserver.template.HtmlComponent;

public class LoginPage extends GlobalPage {
    public LoginPage() {
        super(null);

        this.setPage(new HtmlComponent("auth/login-page"));
    }
}
