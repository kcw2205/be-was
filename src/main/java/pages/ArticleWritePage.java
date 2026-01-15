package pages;

import model.User;
import webserver.template.HtmlComponent;

public class ArticleWritePage extends GlobalPage {
    public ArticleWritePage(User user) {
        super(user);

        HtmlComponent writePage = new HtmlComponent("article/write");

        this.setPage(writePage);
    }
}
