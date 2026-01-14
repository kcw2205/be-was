package model;


import java.time.LocalDateTime;

public class Comment {
    private long id;
    private String authorId;
    private long articleId;
    private LocalDateTime commentAt;
    private String content;

    public Comment(String authorId, long articleId, String content) {
        this.authorId = authorId;
        this.articleId = articleId;
        this.content = content;
    }

    public Comment(long id, String authorId, long articleId, LocalDateTime commentAt, String content) {
        this.id = id;
        this.authorId = authorId;
        this.articleId = articleId;
        this.commentAt = commentAt;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getContent() {
        return content;
    }

    public long getArticleId() {
        return articleId;
    }

    public LocalDateTime getCommentAt() {
        return commentAt;
    }
}
