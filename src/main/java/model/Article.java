package model;

public class Article {
    private long id;
    private String authorId;
    private String imagePath;
    private String content;
    private long likeCount;

    public Article(String authorId, String imagePath, String content) {
        this.authorId = authorId;
        this.imagePath = imagePath;
        this.content = content;
        this.likeCount = 0;
    }

    public Article(long id, String authorId, String imagePath, String content, long likeCount) {
        this.id = id;
        this.authorId = authorId;
        this.imagePath = imagePath;
        this.content = content;
        this.likeCount = likeCount;
    }

    public long getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getContent() {
        return content;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void likeArticle() {
        this.likeCount++;
    }
}
