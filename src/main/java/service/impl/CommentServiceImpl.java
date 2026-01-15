package service.impl;

import dao.CommentDAO;
import dao.UserDAO;
import dto.command.CreateCommentCommand;
import dto.output.CommentDetailOutput;
import exception.ServiceErrorCode;
import model.Article;
import model.Comment;
import model.User;
import service.CommentService;
import webserver.http.HttpException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentServiceImpl implements CommentService {

    private final CommentDAO commentDAO;
    private final UserDAO userDAO;

    public CommentServiceImpl(CommentDAO commentDAO, UserDAO userDAO) {
        this.commentDAO = commentDAO;
        this.userDAO = userDAO;
    }

    // TODO: 쿼리 최적화?
    @Override
    public List<CommentDetailOutput> findByArticleId(Long articleId) throws HttpException {
        List<Comment> comments = commentDAO.findByArticleId(articleId);

        Map<String, User> userMap = new HashMap<>();
        for (Comment comment : comments) {
            String authorId = comment.getAuthorId();
            if (!userMap.containsKey(authorId)) {
                User user = userDAO.findById(authorId)
                    .orElseThrow(ServiceErrorCode.DATA_VALIDATION_ERROR::toException);
                userMap.put(authorId, user);
            }
        }

        return comments.stream()
            .map(comment -> CommentDetailOutput.of(userMap.get(comment.getAuthorId()), comment))
            .toList();
    }

    @Override
    public CommentDetailOutput addCommentToArticle(User user, Article article, CreateCommentCommand command) {
        return CommentDetailOutput.of(
            user,
            commentDAO.save(new Comment(user.getUserId(), article.getId(), command.content()))
        );
    }
}
