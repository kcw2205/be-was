package dto.output;

import model.Comment;
import model.User;

public record CommentDetailOutput(long commentId, long articleId, String profileImagePath, String authorName, String content) {

    public static CommentDetailOutput of(User user, Comment comment) {
        return new CommentDetailOutput(
            comment.getId(),
            comment.getArticleId(),
            user.getProfileImagePath(),
            user.getName(),
            comment.getContent()
        );
    }
}
