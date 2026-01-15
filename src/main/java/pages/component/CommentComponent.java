package pages.component;

import dto.output.CommentDetailOutput;
import webserver.template.HtmlComponent;
import webserver.template.HtmlComponentList;
import webserver.template.RawString;
import webserver.template.Renderable;

import java.util.List;

public class CommentComponent {
    private final Renderable componentList;


    public CommentComponent(List<CommentDetailOutput> commentDetailOutputs) {
        this.componentList = commentDetailOutputs.isEmpty() ? null : convertToComponentList(commentDetailOutputs);
    }

    private Renderable convertToComponentList(List<CommentDetailOutput> commentDetailOutputs) {
        return new HtmlComponentList(
            commentDetailOutputs.stream().map(comment -> {
                HtmlComponent component = new HtmlComponent("comment/single-comment");
                component.setField("authorName", new RawString(comment.authorName()));
                component.setField("profileImagePath", new RawString(comment.profileImagePath()));
                component.setField("content", new RawString(comment.content()));
                return component;
            }).toList()
        );
    }

    public Renderable getComponent() {
        return this.componentList == null ?
            new HtmlComponent("comment/empty-comment-list") :
            this.componentList;
    }
}
