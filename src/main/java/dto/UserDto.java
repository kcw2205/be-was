package dto;

import model.User;

public record UserDto(String userId, String name, String email) {

    public static UserDto of(User user) {
        return new UserDto(
            user.getUserId(),
            user.getName(),
            user.getEmail()
        );
    }
}
