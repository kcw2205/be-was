package pages;

import dto.output.CommentDetailOutput;
import model.Article;
import model.User;
import webserver.template.HtmlComponent;
import webserver.template.HtmlComponentList;
import webserver.template.RawString;

import java.util.List;

public class IndexPage implements Page {
    private HtmlComponent rootComponent;

    public IndexPage(User user, Article article, List<CommentDetailOutput> comments) {
        createComponent(user, article, comments);
    }

    private void createComponent(User user, Article article, List<CommentDetailOutput> comments) {
        this.rootComponent = new HtmlComponent("index");

        HtmlComponent gnb = user != null ?
            new HtmlComponent("gnb/user-menu") :
            new HtmlComponent("gnb/guest-menu");

        if (user != null) {
            gnb.setField("name", new RawString(user.getName()));
        }

        this.rootComponent.setField("menuComponent", gnb);

        HtmlComponent articleComponent = article != null ?
            new HtmlComponent("article/single-article") :
            new HtmlComponent("article/empty-article");

        // TODO: CommentDetailDto 를 사용할까 vs 서비스 로직을 그냥 불러올까
        List<HtmlComponent> commentComponents = comments.stream().map(comment -> {
            HtmlComponent component = new HtmlComponent("comment/single-comment");
            component.setField("authorName", new RawString(comment.authorName()));
            component.setField("profileImagePath", new RawString(comment.profileImagePath()));
            component.setField("content", new RawString(comment.content()));
            return component;
        }).toList();

        articleComponent.setField("commentComponent",
            commentComponents.isEmpty() ?
                new HtmlComponent("comment/empty-comment-list") :
                new HtmlComponentList(commentComponents)
        );
        this.rootComponent.setField("article", articleComponent);
    }

    @Override
    public String renderPage() {
        return this.rootComponent.render();
    }
}
