package pages;

import model.User;
import webserver.template.HtmlComponent;
import webserver.template.RawString;

public class MyPage implements Page {
    HtmlComponent rootComponent;

    public MyPage(User user) {
        this.rootComponent = new HtmlComponent("mypage");
        HtmlComponent gnb = new HtmlComponent("gnb/user-menu");
        gnb.setField("name", new RawString(user.getName()));
        this.rootComponent.setField("menuComponent", gnb);
        this.rootComponent.setField("profileImagePath", new RawString(user.getProfileImagePath()));
        this.rootComponent.setField("name", new RawString(user.getName()));
    }

    @Override
    public String renderPage() {
        return this.rootComponent.render();
    }
}
