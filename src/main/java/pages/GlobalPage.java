package pages;

import model.User;
import pages.component.GlobalNavigationBar;
import webserver.template.HtmlComponent;

public class GlobalPage {
    private final HtmlComponent pageComponent;

    public GlobalPage(User user) {

        this.pageComponent = new HtmlComponent("root");

        this.pageComponent.setField("menuComponent", new GlobalNavigationBar(user).getComponent());
    }

    protected void setPage(HtmlComponent pageComponent) {
        this.pageComponent.setField("page", pageComponent);
    }

    public String renderPage() {
        return this.pageComponent.render();
    }
}
