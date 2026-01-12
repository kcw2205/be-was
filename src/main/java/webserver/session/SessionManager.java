package webserver.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SessionManager {

    public static final String SESSION_ID = "sid";
    public static final Duration SESSION_EXPIRATION = Duration.ofHours(3);

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionManager.class);
    private static final String DAEMON_NAME = "Session-Cleaner-Daemon";

    private final ScheduledExecutorService scheduler;
    private final ConcurrentMap<String, Session> sessions;

    public SessionManager() {
        sessions = new ConcurrentHashMap<>();

        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setName(DAEMON_NAME);
            t.setDaemon(true); // 메인 스레드 종료 시 함께 종료되도록 설정하는 구문
            return t;
        });

        this.start();
    }

    private void start() {
        Runnable cleanupTask = this::removeExpiredSessions;

        scheduler.scheduleWithFixedDelay(cleanupTask, 1, 5, TimeUnit.MINUTES);
    }

    // 세션 ID 를 반환
    public String createSession(Object item) {
        String sid = createSessionId();
        this.sessions.put(sid, new Session(item, LocalDateTime.now().plus(SESSION_EXPIRATION)));
        return sid;
    }

    // TODO: Object 반환...!!! 어떻게할지..? -> Servlet 에서는 어쩔 수 없이 캐스팅했던 것 같은데 더 좋은 방법?
    public Optional<Object> findById(String sid) {
        Session sobj = sessions.get(sid);

        if (sobj == null || sobj.isExpired()) return Optional.empty();

        return Optional.ofNullable(sobj.payload());
    }

    // TODO: UUID 대신에 좋은 다양한 방법 생각해보기
    // 현재로써는 간단하게 UUID 를 받아왔음. 현재로썬 충돌이 가장 덜나며, 간단하기 때문.
    private String createSessionId() {
        return UUID.randomUUID().toString();
    }

    public void clearSession(String value) {
        sessions.remove(value);
    }

    private void removeExpiredSessions() {
        try {
            LOGGER.debug("Started cleaning sessions");

            sessions.forEach((sid, session) -> {
                if (session.isExpired()) {
                    sessions.remove(sid);
                }
            });
        } catch (Exception e) {
            // 스케줄러 내부에서 예외가 발생하면 이후 스케줄링이 멈출 수 있으므로 반드시 catch 해야 함
            LOGGER.error("Failed to remove expired sessions {}", e.getMessage());
        }

        LOGGER.debug("Successfully cleaned expired sessions");
    }
}
