package dao.impl;

import dao.UserDAO;
import model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UserDAOInMemory implements UserDAO {
    private final Map<String, User> users = new HashMap<>();

    @Override
    public User save(User data) {
        String key = UUID.randomUUID().toString();

        users.put(key, data);

        return new User(
            key,
            data.getPassword(),
            data.getName(),
            data.getEmail(),
            data.getProfileImagePath()
        );
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
