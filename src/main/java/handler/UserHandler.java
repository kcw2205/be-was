package handler;

import dto.UserDto;
import dto.command.UserLoginCommand;
import dto.command.UserRegisterCommand;
import dto.command.UserUpdateCommand;
import exception.ServiceErrorCode;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;
import webserver.handling.ResponseEntity;
import webserver.http.HttpException;
import webserver.http.converter.UrlEncodedBodyConverter;
import webserver.http.data.Cookie;
import webserver.http.data.HttpRequest;
import webserver.http.enums.HttpContentType;
import webserver.http.enums.HttpHeaderKey;
import webserver.http.enums.HttpStatusCode;

public class UserHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserHandler.class);

    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity<UserDto> createUser(HttpRequest httpRequest) throws HttpException {

        UserRegisterCommand userRegisterCommand = httpRequest
            .body()
            .mapToRecord(new UrlEncodedBodyConverter(), UserRegisterCommand.class);

        User user = userService.createUser(userRegisterCommand);

        LOGGER.debug("{} added to database.", user);

        return ResponseEntity
            .create(UserDto.of(user), HttpStatusCode.REDIRECT, HttpContentType.APPLICATION_JSON)
            .addHeader(HttpHeaderKey.LOCATION, "/");
    }

    public ResponseEntity<Void> login(HttpRequest httpRequest) throws HttpException {
        UserLoginCommand userLoginCommand = httpRequest
            .body()
            .mapToRecord(new UrlEncodedBodyConverter(), UserLoginCommand.class);

        String sid = userService.login(userLoginCommand);

        // 쿠키 및 응답 구성에 대한 책임은 핸들러가 해야한다고 판단하여 서비스 로직에 추가하진 않음
        Cookie cookie = new Cookie(UserService.SESSION_ID, sid);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return ResponseEntity
            .ok()
            .addCookie(cookie);
    }

    public ResponseEntity<Void> logout(HttpRequest httpRequest) throws HttpException {


        userService.logout(httpRequest);

        Cookie resetCookie = new Cookie(UserService.SESSION_ID, "");

        resetCookie.setPath("/");
        resetCookie.setHttpOnly(true);
        resetCookie.setMaxAge(0);

        return ResponseEntity.ok()
            .addCookie(resetCookie);
    }

    public ResponseEntity<UserDto> me(HttpRequest httpRequest) throws HttpException {

        User user = userService.getCurrentUser(httpRequest);

        if (user == null) {
            throw ServiceErrorCode.NOT_LOGGED_IN.toException();
        }

        return ResponseEntity.ok(UserDto.of(user), HttpContentType.APPLICATION_JSON);
    }

    public ResponseEntity<UserDto> updateUser(HttpRequest httpRequest) throws HttpException {
        UserUpdateCommand command = httpRequest
            .body()
            .mapToRecord(new UrlEncodedBodyConverter(), UserUpdateCommand.class);

        try {
            User user = userService.getCurrentUser(httpRequest);
            User updatedUser = userService.updateUser(user, command);

            userService.syncSession(httpRequest);

            return ResponseEntity.ok(UserDto.of(updatedUser), HttpContentType.APPLICATION_JSON);
        } catch (RuntimeException e) {
            // 업데이트 실패 시, 데이터베이스에 있던 내용과 세션을 다시 동기화 해줘야함. (update 로직 때문)
            // TODO: 불변성을 지키고자해도 동기화를 해주어야하므로 현재로썬 이 방법이 가장 간단 (유지보수에는 비적합)
            userService.syncSession(httpRequest);
            throw e;
        }
    }
}
