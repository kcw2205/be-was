package handler;

import dto.output.ArticleDetail;
import dto.output.CommentDetailOutput;
import exception.ServiceErrorCode;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.ArticlePage;
import pages.ArticleWritePage;
import pages.CommentWritePage;
import pages.LoginPage;
import pages.MyPage;
import pages.RegisterPage;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewHandler.class);
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
        User user;

        long startMs = System.currentTimeMillis();

        try {
            user = userService.getCurrentUser(httpRequest);
        } catch (HttpException e) {
            user = null;
        }

        long userMs = System.currentTimeMillis();

        LOGGER.debug("user find time : {}", userMs - startMs);

        Map<String, String> queryParameters = httpRequest.queryParameters();

        long articleId;

        try {
            articleId = Long.parseLong(queryParameters.get("articleId"));
        } catch (NumberFormatException e) {
            articleId = -1;
        }

        ArticleDetail articleDetail;

        // TODO: 게시글이 없는 경우와, 없는 게시글을 찾아볼 때의 구분
        try {
            articleDetail = ((articleId == -1) ?
                articleService.getDefaultArticle() :
                articleService.getArticleDetailById(articleId))
                .orElseThrow(ServiceErrorCode.RESOURCE_NOT_FOUND::toException);
        } catch (HttpException e) {
            if (articleId != -1) return staticHandler.handleNotFoundPage();
            else articleDetail = null;
        }

        long articleMs = System.currentTimeMillis();
        LOGGER.debug("article find time : {}", articleMs - userMs);

        List<CommentDetailOutput> commentList = List.of();
        if (articleDetail != null) commentList = commentService.findByArticleId(articleDetail.articleId());

        long commentMs = System.currentTimeMillis();
        LOGGER.debug("comment find time : {}", commentMs - articleMs);

        ResponseEntity<String> res = ResponseEntity
            .ok(new ArticlePage(user, articleDetail, commentList).renderPage(), HttpContentType.HTML);

        long renderMs = System.currentTimeMillis();
        LOGGER.debug("rendering time : {}", renderMs - commentMs);

        return res;
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

    public ResponseEntity<String> writeArticlePage(HttpRequest httpRequest) {
        try {
            User user = this.userService.getCurrentUser(httpRequest);

            return ResponseEntity.ok(new ArticleWritePage(user).renderPage(), HttpContentType.HTML);

        } catch (HttpException e) {
            return ResponseEntity.create("로그인 되지 않았습니다.", HttpStatusCode.REDIRECT, HttpContentType.TEXT_PLAIN)
                .addHeader(HttpHeaderKey.LOCATION, "/login");
        }
    }

    public ResponseEntity<String> writeCommentPage(HttpRequest httpRequest) {
        try {
            User user = this.userService.getCurrentUser(httpRequest);

            return ResponseEntity.ok(new CommentWritePage(user).renderPage(), HttpContentType.HTML);

        } catch (HttpException e) {
            return ResponseEntity.create("로그인 되지 않았습니다.", HttpStatusCode.REDIRECT, HttpContentType.TEXT_PLAIN)
                .addHeader(HttpHeaderKey.LOCATION, "/login");
        }
    }

    public ResponseEntity<?> loginPage(HttpRequest httpRequest) {
        try {
            userService.getCurrentUser(httpRequest);

            return ResponseEntity.simple(HttpStatusCode.REDIRECT)
                .addHeader(HttpHeaderKey.LOCATION, "/");

        } catch (HttpException e) {
            return ResponseEntity.ok(new LoginPage().renderPage(), HttpContentType.HTML);
        }
    }

    public ResponseEntity<?> registerPage(HttpRequest httpRequest) {
        try {
            userService.getCurrentUser(httpRequest);

            return ResponseEntity.simple(HttpStatusCode.REDIRECT)
                .addHeader(HttpHeaderKey.LOCATION, "/");

        } catch (HttpException e) {
            return ResponseEntity.ok(new RegisterPage().renderPage(), HttpContentType.HTML);
        }
    }
}
