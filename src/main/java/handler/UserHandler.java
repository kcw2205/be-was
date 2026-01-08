package handler;

import db.UserDatabase;
import dto.LoginDto;
import dto.UserDto;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.handling.ResponseEntity;
import webserver.http.converter.UrlEncodedBodyConverter;
import webserver.http.data.Cookie;
import webserver.http.data.HttpRequest;
import webserver.http.enums.HttpContentType;
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
            .getDataAs(new UrlEncodedBodyConverter(), UserDto.class);

        User user = new User(
            userDto.getUserId(),
            userDto.getPassword(),
            userDto.getName(),
            userDto.getEmail()
        );

        userDatabase.addUser(user);

        log.debug("{} added to database.", user.toString());

        return ResponseEntity
            .builder(UserDto.of(user), HttpStatusCode.REDIRECT, HttpContentType.APPLICATION_JSON)
            .addHeader(HttpHeaderKey.LOCATION, "/");
    }

    public ResponseEntity<String> login(HttpRequest httpRequest) {
        LoginDto loginDto = httpRequest
            .getBody()
            .getDataAs(new FormDataConverter(), LoginDto.class);

        System.out.println(loginDto.toString());

        User user = userDatabase.findUserById(loginDto.getUserId());

        if (user == null || !user.getPassword().equals(loginDto.getPassword())) {
            return ResponseEntity.builder("Invalid username or password", HttpStatusCode.FORBIDDEN, HttpContentType.TEXT_PLAIN);
        }

        String sid = this.sessionManager.createSession(user);

        Cookie cookie = new Cookie(SessionManager.SESSION_ID, sid);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return ResponseEntity
            .simple(HttpStatusCode.OK)
            .addCookie(cookie);
    }

    public ResponseEntity<?> me(HttpRequest httpRequest) {
        Cookie sessionCookie = httpRequest.getCookieByName(SessionManager.SESSION_ID).orElse(null);

        // TODO: 로직이 반복되는 느낌 + empty Response 해도 괜찮을지.
        if (sessionCookie == null) {
            return ResponseEntity.simple(HttpStatusCode.UNAUTHORIZED);
        }

        log.debug("{} is user cookie value", sessionCookie.getValue());

        User user = (User) sessionManager.findById(sessionCookie.getValue()).orElse(null);

        if (user == null) {
            return ResponseEntity.simple(HttpStatusCode.UNAUTHORIZED);
        }

        return ResponseEntity.ok(UserDto.of(user), HttpContentType.APPLICATION_JSON);
    }
}
