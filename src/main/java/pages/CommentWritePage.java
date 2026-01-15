package pages;

import model.User;
import webserver.template.HtmlComponent;

public class CommentWritePage extends GlobalPage {
    public CommentWritePage(User user) {
        super(user);
        HtmlComponent root = new HtmlComponent("comment/write");
        setPage(root);
    }
}
