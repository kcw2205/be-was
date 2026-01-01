package handler;

import db.UserDatabase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.data.HttpRequest;
import webserver.data.ResponseEntity;
import webserver.data.enums.HttpStatusCode;

public class UserHandler implements RequestHandler {

    public static class UserResponseDto implements ResponseEntity {
        private String userId;
        private String password;
        private String name;
        private String email;
        private HttpStatusCode httpStatusCode;

        public static UserResponseDto of(User user) {
            return new UserResponseDto(
                    user.getUserId(),
                    user.getPassword(),
                    user.getName(),
                    user.getEmail()
            );
        }

        // TODO: 확장 관련 개선 필요
        public static UserResponseDto notFound() {
            return new UserResponseDto();
        }

        private UserResponseDto(String userId, String password, String name, String email) {
            this.userId = userId;
            this.password = password;
            this.name = name;
            this.email = email;
            this.httpStatusCode = HttpStatusCode.OK;
        }

        private UserResponseDto() {
            this.httpStatusCode = HttpStatusCode.NOT_FOUND;
        }

        @Override
        public HttpStatusCode getHttpStatusCode() {
            return httpStatusCode;
        }

        // TODO: Reflection API 찾아보기!
        // TODO: JSON 에서 숫자인 건 판단해서
        @Override
        public byte[] getSerializedData() {
            return ("{" + "\"userId\": \"" + this.userId +
                    "\", \"password\": \"" + this.password +
                    "\", \"name\": \"" + this.name +
                    "\", \"email\": \"" + this.email + "\"}")
                    .getBytes();
        }

        @Override
        public String getContentType() {
            return "application/json";
        }
    }

    private static final Logger log = LoggerFactory.getLogger(UserHandler.class);

    private final UserDatabase userDatabase;

    public UserHandler(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    @Override
    public ResponseEntity handleRequest(HttpRequest httpRequest) {

        var queryParams = httpRequest.getQueryParameters();

        var user = new User(
                queryParams.get("userId"),
                queryParams.get("password"),
                queryParams.get("name"),
                queryParams.get("email")
        );

        userDatabase.addUser(user);

        log.debug(user.toString() + " added to database.");

        return UserResponseDto.of(user);
    }
}
