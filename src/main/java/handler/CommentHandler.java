package handler;

import dto.command.CommentByArticleQuery;
import dto.command.CreateCommentCommand;
import dto.output.CommentDetailOutput;
import exception.ServiceErrorCode;
import model.Article;
import model.User;
import service.ArticleService;
import service.CommentService;
import service.UserService;
import webserver.handling.ResponseEntity;
import webserver.http.HttpException;
import webserver.http.converter.UrlEncodedBodyConverter;
import webserver.http.data.HttpRequest;
import webserver.http.enums.HttpContentType;

import java.util.List;

public class CommentHandler {
    private final UserService userService;
    private final ArticleService articleService;
    private final CommentService commentService;

    public CommentHandler(UserService userService, ArticleService articleService, CommentService commentService) {
        this.userService = userService;
        this.articleService = articleService;
        this.commentService = commentService;
    }

    // TODO: 시간 없어서 Path Variable 은 구현하지 않음..
    public ResponseEntity<List<CommentDetailOutput>> findByArticleId(HttpRequest httpRequest) throws HttpException {
        CommentByArticleQuery query = httpRequest.body().mapToRecord(new UrlEncodedBodyConverter(), CommentByArticleQuery.class);

        List<CommentDetailOutput> commentDtoList = commentService.findByArticleId(query.articleId());

        return ResponseEntity.ok(commentDtoList, HttpContentType.APPLICATION_JSON);
    }

    public ResponseEntity<CommentDetailOutput> createComment(HttpRequest httpRequest) throws HttpException {
        CreateCommentCommand command = httpRequest.body()
            .mapToRecord(new UrlEncodedBodyConverter(), CreateCommentCommand.class);

        User user = userService.getCurrentUser(httpRequest);
        Article article = articleService.findById(command.articleId())
            .orElseThrow(ServiceErrorCode.RESOURCE_NOT_FOUND::toException);

        CommentDetailOutput comment = commentService.addCommentToArticle(user, article, command);

        return ResponseEntity.ok(comment, HttpContentType.APPLICATION_JSON);
    }
}
