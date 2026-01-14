package dto.command;

public record UserRegisterCommand(String userId, String name, String email, String password) {
}
