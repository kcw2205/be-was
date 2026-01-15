package service;

import dto.command.CreateCommentCommand;
import dto.output.CommentDetailOutput;
import model.Article;
import model.User;
import webserver.http.HttpException;

import java.util.List;

public interface CommentService {

    List<CommentDetailOutput> findByArticleId(Long articleId) throws HttpException;

    CommentDetailOutput addCommentToArticle(User user, Article article, CreateCommentCommand command);
}
