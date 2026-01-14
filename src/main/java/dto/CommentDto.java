package dto;

import model.Comment;

public record CommentDto(long commentId, String authorName, String content) {

    public static CommentDto of(Comment comment) {
        return new CommentDto(
            comment.getId(),
            comment.getAuthorId(),
            comment.getContent()
        );
    }
}
