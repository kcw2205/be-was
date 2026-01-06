package dto;

public class LoginDto {
    private String userId;
    private String password;

    public LoginDto() {
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "{" + "\"userId\" : \"" + userId + "\", \"password\":\"" + password + "\"}";
    }
}
