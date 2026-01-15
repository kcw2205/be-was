package service.impl;

import dao.ArticleDAO;
import dao.UserDAO;
import dto.command.CreateArticleCommand;
import dto.output.ArticleDetail;
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
    public Optional<Article> getArticleById(long id) {
        return articleDAO.findById(id);
    }

    @Override
    public void likeArticle(long articleId) throws HttpException {
        Article article = articleDAO.findById(articleId)
            .orElseThrow(ServiceErrorCode.RESOURCE_NOT_FOUND::toException);

        articleDAO.increaseLikeCount(article.getId());
    }

    @Override
    public Optional<ArticleDetail> getDefaultArticle() throws HttpException {
        // 최신 글이 없을 경우 404 예외를 명시적으로 던집니다.
        ArticleDetail detail = articleDAO.findRecentArticleDetail()
            .orElseThrow(ServiceErrorCode.RESOURCE_NOT_FOUND::toException);

        return Optional.of(applySecurity(detail));
    }

    @Override
    public Optional<ArticleDetail> getArticleDetailById(long id) throws HttpException {
        // 특정 ID 조회 실패 시 예외 처리
        ArticleDetail detail = articleDAO.findArticleDetailById(id)
            .orElseThrow(ServiceErrorCode.RESOURCE_NOT_FOUND::toException);

        return Optional.of(applySecurity(detail));
    }

    // XSS 보안 처리만 담당하는 로직 분리
    private ArticleDetail applySecurity(ArticleDetail detail) {
        return new ArticleDetail(
            detail.articleId(),
            detail.authorName(),
            detail.authorProfileImagePath(),
            securityService.escapeXss(detail.content()), // 필드만 교체
            detail.imagePath(),
            detail.likeCount(),
            detail.prevId(),
            detail.nextId()
        );
    }
}
