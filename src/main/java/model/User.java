package model;

public class User {
    private static final String DEFAULT_PROFILE_IMAGE = "profile.png";
    private String userId;
    private String password;
    private String name;
    private String email;
    private String profileImagePath = DEFAULT_PROFILE_IMAGE;

    public User(String userId, String password, String name, String email, String profileImagePath) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.profileImagePath = profileImagePath;
    }

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.profileImagePath = DEFAULT_PROFILE_IMAGE;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }

    public void resetProfileImage() {
        this.profileImagePath = DEFAULT_PROFILE_IMAGE;
    }

    public void updateProfileImage(String profileImagePath) {
        this.profileImagePath = DEFAULT_PROFILE_IMAGE;
    }

    public void updateUserInfo(String name, String password) {
        this.name = name;
        this.password = password;
    }

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

    public String getProfileImagePath() {
        return profileImagePath;
    }
}
