package service;

import dto.command.CreateArticleCommand;
import dto.output.ArticleDetail;
import model.Article;
import model.User;
import webserver.http.HttpException;

import java.util.Optional;

public interface ArticleService {

    Article createArticle(User user, CreateArticleCommand command);

    Optional<Article> getArticleById(long id);

    void likeArticle(long articleId) throws HttpException;

    Optional<ArticleDetail> getDefaultArticle() throws HttpException;

    Optional<ArticleDetail> getArticleDetailById(long id) throws HttpException;
}
