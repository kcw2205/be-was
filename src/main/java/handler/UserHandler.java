package handler;

import db.UserDatabase;
import dto.LoginDto;
import dto.UserDto;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.handling.ResponseEntity;
import webserver.http.converter.FormDataConverter;
import webserver.http.data.HttpRequest;
import webserver.http.enums.HttpHeaderKey;
import webserver.http.enums.HttpStatusCode;

public class UserHandler {

    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);

    private final UserDatabase userDatabase;

    public UserHandler(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    // TODO: DTO 검증로직 추가
    public ResponseEntity<UserDto> createUser(HttpRequest httpRequest) {

        UserDto userDto = httpRequest.getBody().getDataAs(new FormDataConverter(), UserDto.class);

        User user = new User(
            userDto.getUserId(),
            userDto.getPassword(),
            userDto.getName(),
            userDto.getEmail()
        );

        userDatabase.addUser(user);

        log.debug("{} added to database.", user.toString());

        return ResponseEntity
            .builder(UserDto.of(user), HttpStatusCode.REDIRECT, "application/json")
            .addHeader(HttpHeaderKey.LOCATION, "/");
    }

    public ResponseEntity<String> login(HttpRequest httpRequest) {
        LoginDto loginDto = httpRequest.getBody().getDataAs(new FormDataConverter(), LoginDto.class);

        User user = userDatabase.findUserById(loginDto.getUserId());

        if (user == null) {
            return ResponseEntity.builder("Invalid username or password", HttpStatusCode.FORBIDDEN, "text/plain");
        }

        return ResponseEntity.ok("Login success", "text/plain");
    }
}
