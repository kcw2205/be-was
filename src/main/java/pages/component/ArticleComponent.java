package pages.component;

import dto.output.ArticleDetail;
import dto.output.CommentDetailOutput;
import webserver.template.HtmlComponent;
import webserver.template.RawString;

import java.util.List;

public class ArticleComponent {
    private final HtmlComponent component;

    public ArticleComponent(ArticleDetail articleDetail, List<CommentDetailOutput> commentDetailOutputs) {

        this.component = articleDetail != null ?
            new HtmlComponent("article/single-article") :
            new HtmlComponent("article/empty-article");

        if (articleDetail != null) setArticleDetail(articleDetail, commentDetailOutputs);


    }

    private void setArticleDetail(ArticleDetail articleDetail, List<CommentDetailOutput> commentDetailOutputs) {
        component.setField("commentComponent", new CommentComponent(commentDetailOutputs).getComponent());

        component.setField("authorName", new RawString(articleDetail.authorName()));
        component.setField("articleId", new RawString(String.valueOf(articleDetail.articleId())));
        component.setField("authorProfileImagePath", new RawString(articleDetail.authorProfileImagePath()));
        component.setField("articleImagePath", new RawString(articleDetail.imagePath()));
        component.setField("content", new RawString(articleDetail.content()));
        component.setField("likeCount", new RawString(String.valueOf(articleDetail.likeCount())));
        component.setField("prevArticleStatus", new RawString(articleDetail.prevId() == -1 ? "inactive" : "active"));
        component.setField("nextArticleStatus", new RawString(articleDetail.nextId() == -1 ? "inactive" : "active"));
        component.setField("prevArticleId", new RawString(String.valueOf(articleDetail.prevId())));
        component.setField("nextArticleId", new RawString(String.valueOf(articleDetail.nextId())));
    }

    public HtmlComponent getComponent() {
        return component;
    }
}
