package dto;

import model.User;

public record UserResponseDto(String userId, String password, String name, String email) {
    public static UserResponseDto of(User user) {
        return new UserResponseDto(
            user.getUserId(),
            user.getPassword(),
            user.getName(),
            user.getEmail()
        );
    }

    @Override
    public String toString() {
        return ("{" + "\"userId\": \"" + this.userId +
            "\", \"password\": \"" + this.password +
            "\", \"name\": \"" + this.name +
            "\", \"email\": \"" + this.email + "\"}");
    }
}
