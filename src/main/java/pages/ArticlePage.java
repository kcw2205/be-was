package pages;

import dto.output.ArticleDetail;
import dto.output.CommentDetailOutput;
import model.User;
import pages.component.ArticleComponent;
import webserver.template.HtmlComponent;

import java.util.List;

public class ArticlePage extends GlobalPage {
    public ArticlePage(User user, ArticleDetail articleDetail, List<CommentDetailOutput> comments) {
        super(user);

        HtmlComponent page = new HtmlComponent("article/article-page");

        HtmlComponent articleComponent = new ArticleComponent(articleDetail, comments).getComponent();

        page.setField("article", articleComponent);


        this.setPage(page);
    }
}
