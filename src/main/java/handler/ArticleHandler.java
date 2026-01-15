package handler;

import dto.ArticleDto;
import dto.command.CreateArticleCommand;
import dto.command.LikeArticleCommand;
import model.Article;
import model.User;
import service.ArticleService;
import service.UserService;
import webserver.handling.ResponseEntity;
import webserver.http.HttpException;
import webserver.http.converter.UrlEncodedBodyConverter;
import webserver.http.data.HttpRequest;
import webserver.http.enums.HttpContentType;

public class ArticleHandler {

    private final UserService userService;
    private final ArticleService articleService;

    public ArticleHandler(UserService userService, ArticleService articleService) {
        this.userService = userService;
        this.articleService = articleService;
    }

    public ResponseEntity<ArticleDto> createArticle(HttpRequest httpRequest) throws HttpException {
        User user = userService.getCurrentUser(httpRequest);

        CreateArticleCommand command = httpRequest
            .body()
            .mapToRecord(new UrlEncodedBodyConverter(), CreateArticleCommand.class);

        Article article = articleService.createArticle(user, command);

        return ResponseEntity.ok(ArticleDto.of(article), HttpContentType.APPLICATION_JSON);
    }

    public ResponseEntity<Void> likeArticle(HttpRequest httpRequest) throws HttpException {
        LikeArticleCommand command = httpRequest
            .body()
            .mapToRecord(new UrlEncodedBodyConverter(), LikeArticleCommand.class);

        articleService.likeArticle(command.articleId());

        return ResponseEntity.ok();
    }
}
