package service.impl;

import dao.UserDAO;
import dto.command.UserLoginCommand;
import dto.command.UserRegisterCommand;
import exception.ServiceErrorCode;
import model.User;
import service.SecurityService;
import service.UserService;
import webserver.http.HttpException;
import webserver.http.data.Cookie;
import webserver.http.data.HttpRequest;
import webserver.session.SessionManager;

/**
 * Custom Http Exception을 왠만해선 던지지 않도록 하기.
 * 던지도록, 받는 곳에서 처리하도록 Checked Exception으로 만든 새로운 무언가를 던지도록 만들자.
 */
public class UserServiceImpl implements UserService {
    private static final int LEAST_PARAMETER_LENGTH = 4;
    private final UserDAO userDAO;
    private final SessionManager sessionManager;
    private final SecurityService securityService;

    public UserServiceImpl(UserDAO userDAO, SessionManager sessionManager, SecurityService securityService) {
        this.userDAO = userDAO;
        this.sessionManager = sessionManager;
        this.securityService = securityService;
    }

    @Override
    public User getCurrentUser(HttpRequest httpRequest) throws HttpException {
        Cookie cookie = httpRequest
            .getCookieByName(UserService.SESSION_ID)
            .orElseThrow(ServiceErrorCode.NOT_LOGGED_IN::toException);

        String sid = cookie.getValue();

        if (sid == null) {
            throw ServiceErrorCode.NOT_LOGGED_IN.toException();
        }

        return (User) sessionManager
            .findById(sid)
            .orElseThrow(ServiceErrorCode.NOT_LOGGED_IN::toException);
    }

    @Override
    public User createUser(UserRegisterCommand command) throws HttpException {
        // 중복 User 의 경우 회원가입 제한
        if (userDAO.findById(command.userId()).isPresent()) {
            throw ServiceErrorCode.SAME_ID_EXISTS.toException();
        }

        // 중복 닉네임일 경우 회원가입 제한
        if (userDAO.findByNickname(command.name()).isPresent()) {
            throw ServiceErrorCode.SAME_NAME_EXISTS.toException();
        }

        User user = new User(
            command.userId(),
            command.password(),
            securityService.escapeXss(command.name()),
            command.email()
        );

        try {
            return userDAO.save(user);
        } catch (Exception e) {
            throw ServiceErrorCode.SAME_NAME_EXISTS.toException();
        }
    }

    @Override
    public String login(UserLoginCommand userLoginCommand) throws HttpException {
        User user = userDAO.findById(userLoginCommand.userId())
            .orElseThrow(ServiceErrorCode.USER_NOT_FOUND::toException);

        if (!user.getPassword().equals(userLoginCommand.password())) {
            throw ServiceErrorCode.WRONG_PASSWORD.toException();
        }

        return this.sessionManager.createSession(user);
    }

    @Override
    public void logout(HttpRequest httpRequest) throws HttpException {
        Cookie cookie = httpRequest
            .getCookieByName(UserService.SESSION_ID)
            .orElseThrow(ServiceErrorCode.NOT_LOGGED_IN::toException);

        sessionManager.clearSession(cookie.getValue());
    }
}
