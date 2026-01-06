package dto;

import model.User;

public class UserDto {
    private String userId;
    private String password;
    private String name;
    private String email;

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public UserDto() {
    }

    public UserDto(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public static UserDto of(User user) {
        return new UserDto(
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
