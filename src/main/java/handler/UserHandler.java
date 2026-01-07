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
import webserver.session.SessionManager;

public class UserHandler {

    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);

    private final UserDatabase userDatabase;
    private final SessionManager sessionManager;

    public UserHandler(UserDatabase userDatabase, SessionManager sessionManager) {
        this.userDatabase = userDatabase;
        this.sessionManager = sessionManager;
    }

    // TODO: DTO 검증로직 추가
    public ResponseEntity<UserDto> createUser(HttpRequest httpRequest) {

        UserDto userDto = httpRequest
            .getBody()
            .getDataAs(new FormDataConverter(), UserDto.class);

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
        LoginDto loginDto = httpRequest
            .getBody()
            .getDataAs(new FormDataConverter(), LoginDto.class);

        System.out.println(loginDto.toString());

        User user = userDatabase.findUserById(loginDto.getUserId());

        if (user == null || !user.getPassword().equals(loginDto.getPassword())) {
            return ResponseEntity.builder("Invalid username or password", HttpStatusCode.FORBIDDEN, "text/plain");
        }

        String sid = this.sessionManager.createSession(user);

        return ResponseEntity
            .ok("Login success", "text/plain")
            .addCookie(SessionManager.SESSION_ID, sid, "/");
    }

    public ResponseEntity<?> me(HttpRequest httpRequest) {
        String sid = httpRequest.getCookieValue(SessionManager.SESSION_ID);

        log.debug("{} is user cookie value", sid);

        // TODO: 로직이 반복되는 느낌 + empty Response 해도 괜찮을지.
        if (sid == null) {
            return ResponseEntity.empty(HttpStatusCode.UNAUTHORIZED);
        }

        User user = (User) sessionManager.findById(sid);

        if (user == null) {
            return ResponseEntity.empty(HttpStatusCode.UNAUTHORIZED);
        }

        return ResponseEntity.ok(UserDto.of(user), "application/json");
    }
}
