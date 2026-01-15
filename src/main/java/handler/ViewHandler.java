package handler;

import dto.output.CommentDetailOutput;
import exception.ServiceErrorCode;
import model.Article;
import model.User;
import pages.IndexPage;
import pages.MyPage;
import service.ArticleService;
import service.CommentService;
import service.UserService;
import webserver.handling.ResponseEntity;
import webserver.http.HttpException;
import webserver.http.data.HttpRequest;
import webserver.http.enums.HttpContentType;
import webserver.http.enums.HttpHeaderKey;
import webserver.http.enums.HttpStatusCode;
import webserver.resources.StaticHandler;

import java.util.List;
import java.util.Map;

public class ViewHandler {

    private final StaticHandler staticHandler;
    private final UserService userService;
    private final ArticleService articleService;
    private final CommentService commentService;

    // TODO: 어거지식 확장?
    public ViewHandler(StaticHandler staticHandler, UserService userService, ArticleService articleService, CommentService commentService) {
        this.staticHandler = staticHandler;
        this.userService = userService;
        this.articleService = articleService;
        this.commentService = commentService;
    }

    // TODO: 이 경우를 좀 더 예쁘게 처리할 순 없을까?
    public ResponseEntity<String> indexPage(HttpRequest httpRequest) throws HttpException {
        Map<String, String> queryParameters = httpRequest.queryParameters();

        long articleId;
        long prev;

        try {
            articleId = Long.parseLong(queryParameters.get("articleId"));
        } catch (NumberFormatException e) {
            articleId = -1;
        }

        Article article;
        User user;

        try {
            article = (articleId == -1) ?
                articleService.findFirstOrderByIdDesc().orElse(null) :
                articleService.findById(articleId).orElseThrow(ServiceErrorCode.RESOURCE_NOT_FOUND::toException);
        } catch (HttpException e) {
            return staticHandler.handleNotFoundPage();
        }

        List<CommentDetailOutput> commentList = List.of();


        if (article != null) {
            commentList = commentService.findByArticleId(article.getId());
        }

        try {
            user = userService.getCurrentUser(httpRequest);
        } catch (HttpException e) {
            user = null;
        }

        return ResponseEntity
            .ok(new IndexPage(user, article, commentList).renderPage(), HttpContentType.HTML);
    }

    public ResponseEntity<String> myPage(HttpRequest httpRequest) {
        try {
            User user = this.userService.getCurrentUser(httpRequest);

            return ResponseEntity
                .ok(new MyPage(user).renderPage(), HttpContentType.HTML);
        } catch (HttpException e) {
            return ResponseEntity.create("", HttpStatusCode.REDIRECT, HttpContentType.HTML)
                .addHeader(HttpHeaderKey.LOCATION, "/");
        }
    }

    public ResponseEntity<String> writePage(HttpRequest httpRequest) {

    }
}
