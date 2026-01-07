package webserver.session;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private record SessionObject(Object payload, LocalDateTime expiration) {

        public boolean isExpired() {
            return expiration.isBefore(LocalDateTime.now());
        }
    }

    public static final String SESSION_ID = "SID";
    public static final Duration SESSION_EXPIRATION = Duration.ofHours(3);
    private final Map<String, SessionObject> sessions;

    public SessionManager() {
        sessions = new HashMap<>();
    }

    // 세션 ID 를 반환
    public String createSession(Object item) {
        String sid = createSessionId();
        this.sessions.put(sid, new SessionObject(item, LocalDateTime.now().plus(SESSION_EXPIRATION)));
        return sid;
    }

    // TODO: Object 반환...!!! 어떻게할지..? -> Servlet 에서는 어쩔 수 없이 캐스팅했던 것 같은데 더 좋은 방법?
    public Object findById(String sid) {
        SessionObject sobj = sessions.get(sid);

        if (sobj.isExpired()) return null;

        return sobj.payload();
    }

    // TODO: UUID 대신에 좋은 다양한 방법 생각해보기
    // 현재로써는 간단하게 UUID 를 받아왔음. 현재로썬 충돌이 가장 덜나며, 간단하기 때문.
    private String createSessionId() {
        return UUID.randomUUID().toString();
    }
}
