package pages;

import model.User;
import webserver.template.HtmlComponent;
import webserver.template.RawString;

public class MyPage extends GlobalPage {

    public MyPage(User user) {
        super(user);
        HtmlComponent page = new HtmlComponent("my/my-page");

        page.setField("profileImagePath", new RawString(user.getProfileImagePath()));
        page.setField("name", new RawString(user.getName()));

        this.setPage(page);
    }
}
