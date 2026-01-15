package model;

import exception.ServiceErrorCode;
import webserver.http.HttpException;

public class User {
    private static final String DEFAULT_PROFILE_IMAGE = "profile.png";
    private static final int LEAST_PARAMETER_LENGTH = 4;
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

    public User(String userId, String password, String name, String email) throws HttpException {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.profileImagePath = DEFAULT_PROFILE_IMAGE;

        if (!validateUserRegisterCommand()) {
            throw ServiceErrorCode.BAD_REQUEST_FORMAT.toException();
        }
    }

    private boolean validateUserRegisterCommand() {
        boolean idValid = this.userId != null && this.userId.length() >= LEAST_PARAMETER_LENGTH;
        boolean nameValid = this.name != null && this.name.length() >= LEAST_PARAMETER_LENGTH;
        boolean emailValid = this.email != null && this.email.length() >= LEAST_PARAMETER_LENGTH;
        boolean passwordValid = this.password != null && this.password.length() >= LEAST_PARAMETER_LENGTH;

        return idValid && nameValid && emailValid && passwordValid;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }

    public void updateUserInfo(String name, String password, String profileImagePath) throws HttpException {
        this.name = name;
        this.password = password;
        if (profileImagePath.isBlank()) {
            this.profileImagePath = DEFAULT_PROFILE_IMAGE;
        }
        else {
            this.profileImagePath = profileImagePath;
        }

        if (!validateUserRegisterCommand()) {
            throw ServiceErrorCode.BAD_REQUEST_FORMAT.toException();
        }
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
