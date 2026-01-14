package dao.impl;

import dao.UserDAO;
import model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserDAOInMemory implements UserDAO {
    private final Map<String, User> users = new HashMap<>();

    @Override
    public void addRecord(String key, User data) {
        users.put(key, data);
    }

    @Override
    public Optional<User> findById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return users.values().stream()
            .filter((user) -> user.getName().equals(nickname))
            .findAny();
    }
}
