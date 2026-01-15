package service;

import dto.command.UserLoginCommand;
import dto.command.UserRegisterCommand;
import dto.command.UserUpdateCommand;
import model.User;
import webserver.http.HttpException;
import webserver.http.data.HttpRequest;

public interface UserService {
    String SESSION_ID = "sid";

    // TODO: 이렇게 HTTP 단과 결합해도 괜찮은지 의논 필요 -> 필터 구현해보기
    User getCurrentUser(HttpRequest request) throws HttpException;

    void syncSession(HttpRequest request) throws HttpException;

    User createUser(UserRegisterCommand userRegisterCommand) throws HttpException;

    /**
     * 서버측에서 유저가 로그인했음을 기억하도록 하는 로직을 수행한다.
     *
     * @param userLoginCommand 로그인 DTO
     * @return 서버 측에서 저장한 세션 정보 혹은 토큰값을 반환한다.
     */
    String login(UserLoginCommand userLoginCommand) throws HttpException;

    void logout(HttpRequest request) throws HttpException;

    User updateUser(User user, UserUpdateCommand command) throws HttpException;


}
