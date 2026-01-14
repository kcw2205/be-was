package service;

import dto.command.CreateArticleCommand;
import model.Article;
import model.User;
import webserver.http.HttpException;

import java.util.Optional;

public interface ArticleService {

    Article createArticle(User user, CreateArticleCommand command);

    void likeArticle(long articleId) throws HttpException;

    Optional<Article> findFirstOrderByIdDesc();

    Optional<Article> findById(long id);

    Optional<Article> findPreviousArticle(long id);

    Optional<Article> findNextArticle(long id);
}
