package pages;

import webserver.template.HtmlComponent;

public class RegisterPage extends GlobalPage{
    public RegisterPage() {
        super(null);
        this.setPage(new HtmlComponent("auth/register-page"));
    }
}
