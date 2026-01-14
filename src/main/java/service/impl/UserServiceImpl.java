package service.impl;

import dao.UserDAO;
import dto.command.UserLoginCommand;
import dto.command.UserRegisterCommand;
import exception.ServiceErrorCode;
import model.User;
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

    public UserServiceImpl(UserDAO userDAO, SessionManager sessionManager) {
        this.userDAO = userDAO;
        this.sessionManager = sessionManager;
    }

    private boolean validateUserRegisterCommand(UserRegisterCommand userRegisterCommand) {
        boolean idValid = userRegisterCommand.userId() != null && userRegisterCommand.userId().length() >= LEAST_PARAMETER_LENGTH;
        boolean nameValid = userRegisterCommand.name() != null && userRegisterCommand.name().length() >= LEAST_PARAMETER_LENGTH;
        boolean emailValid = userRegisterCommand.email() != null && userRegisterCommand.email().length() >= LEAST_PARAMETER_LENGTH;
        boolean passwordValid = userRegisterCommand.password() != null && userRegisterCommand.password().length() >= LEAST_PARAMETER_LENGTH;

        return idValid && nameValid && emailValid && passwordValid;
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
    public User createUser(UserRegisterCommand userDto) throws HttpException {
        if (!validateUserRegisterCommand(userDto)) {
            throw ServiceErrorCode.BAD_REQUEST_FORMAT.toException();
        }

        // 중복 User 의 경우 회원가입 제한
        if (userDAO.findById(userDto.userId()).isPresent()) {
            throw ServiceErrorCode.SAME_ID_EXISTS.toException();
        }

        // 중복 닉네임일 경우 회원가입 제한
        if (userDAO.findByNickname(userDto.name()).isPresent()) {
            throw ServiceErrorCode.SAME_NAME_EXISTS.toException();
        }

        User user = new User(
            userDto.userId(),
            userDto.password(),
            userDto.name(),
            userDto.email()
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
