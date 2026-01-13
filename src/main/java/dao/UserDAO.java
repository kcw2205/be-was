package dao;

import model.User;

import java.util.Optional;

public interface UserDAO extends DataAccessObject<User, String> {

    Optional<User> findByNickname(String nickname);
}
