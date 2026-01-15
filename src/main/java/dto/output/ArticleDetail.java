package dto.output;

/**
 *
 * @param articleId
 * @param authorName
 * @param content
 * @param prevId 없으면 -1 반환
 * @param nextId 없으면 -1 반환
 */
public record ArticleDetail(
    long articleId,
    String authorName,
    String authorProfileImagePath,
    String content,
    String imagePath,
    long likeCount,
    long prevId,
    long nextId) {
}
