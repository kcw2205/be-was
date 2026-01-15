package dto.output;

import model.Comment;
import model.User;

public record CommentDetailOutput(long commentId, String profileImagePath, String authorName, String content) {

    public static CommentDetailOutput of(User user, Comment comment) {
        return new CommentDetailOutput(
            comment.getId(),
            user.getProfileImagePath(),
            user.getName(),
            comment.getContent()
        );
    }
}
