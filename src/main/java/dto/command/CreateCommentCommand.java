package dto.command;

public record CreateCommentCommand(long articleId, String content) {
}
