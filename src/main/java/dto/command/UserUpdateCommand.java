package dto.command;

public record UserUpdateCommand(boolean isImageDeleted, String imagePath, String name, String password) {
}
