package dto;

import model.Article;

public record ArticleDto(long articleId, String authorId, String imagePath, String content, long likeCount) {

    public static ArticleDto of(Article article) {
        return new ArticleDto(
            article.getId(),
            article.getAuthorId(),
            article.getImagePath(),
            article.getContent(),
            article.getLikeCount()
        );
    }
}
