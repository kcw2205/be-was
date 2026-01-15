package service.impl;

import dao.ArticleDAO;
import dao.UserDAO;
import dto.command.CreateArticleCommand;
import exception.ServiceErrorCode;
import model.Article;
import model.User;
import service.ArticleService;
import service.SecurityService;
import webserver.http.HttpException;

import java.util.Optional;

public class ArticleServiceImpl implements ArticleService {

    private final ArticleDAO articleDAO;
    private final UserDAO userDAO;
    private final SecurityService securityService;

    public ArticleServiceImpl(ArticleDAO articleDAO, UserDAO userDAO, SecurityService securityService) {
        this.articleDAO = articleDAO;
        this.userDAO = userDAO;
        this.securityService = securityService;
    }

    @Override
    public Article createArticle(User user, CreateArticleCommand command) {

        return articleDAO
            .save(new Article(user.getUserId(), command.imagePath(), command.content()));
    }

    @Override
    public void likeArticle(long articleId) throws HttpException {
        Article article = articleDAO.findById(articleId)
            .orElseThrow(ServiceErrorCode.RESOURCE_NOT_FOUND::toException);

        article.likeArticle();

        articleDAO.save(article);
    }

    @Override
    public Optional<Article> findFirstOrderByIdDesc() {
        return articleDAO.findFirstOrderByIdDesc();
    }

    @Override
    public Optional<Article> findById(long id) {
        return articleDAO.findById(id);
    }

    @Override
    public Optional<Article> findPreviousArticle(long id) {
        return articleDAO.findPreviousById(id);
    }

    @Override
    public Optional<Article> findNextArticle(long id) {
        return articleDAO.findNextById(id);
    }
}
