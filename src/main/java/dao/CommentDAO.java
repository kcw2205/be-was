package dao;

import model.Comment;

import java.util.List;

public interface CommentDAO extends DataAccessObject<Comment, Long> {

    List<Comment> findByAuthorId(long authorId);

    List<Comment> findByArticleId(long articleId);
}
