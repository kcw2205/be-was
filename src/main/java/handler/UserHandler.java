package handler;

import db.UserDatabase;
import dto.UserResponseDto;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.handling.ResponseEntity;
import webserver.http.data.HttpRequest;

public class UserHandler {

    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);

    private final UserDatabase userDatabase;

    public UserHandler(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    public ResponseEntity<UserResponseDto> createUser(HttpRequest httpRequest) {

        var queryParams = httpRequest.getQueryParameters();

        var user = new User(
            queryParams.get("userId"),
            queryParams.get("password"),
            queryParams.get("name"),
            queryParams.get("email")
        );

        userDatabase.addUser(user);

        log.debug("{} added to database.", user.toString());

        return ResponseEntity.ok(UserResponseDto.of(user), "application/json");
    }
}
