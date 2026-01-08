package webserver.session;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    private final SessionManager sessionManager = new SessionManager();

    @Test
    @DisplayName("세션을 생성하고 ID로 조회할 수 있어야 한다")
    void createAndFindSession() {
        // given
        String userPayload = "User123";

        // when
        String sessionId = sessionManager.createSession(userPayload);
        Optional<Object> retrieved = sessionManager.findById(sessionId);

        // then
        assertNotNull(sessionId);
        assertTrue(retrieved.isPresent());
        assertEquals(userPayload, retrieved.get());
    }

    @Test
    @DisplayName("존재하지 않는 세션 ID로 조회하면 빈 값을 반환해야 한다")
    void findUnknownSession() {
        // given
        String unknownId = "invalid-uuid-string";

        // when
        Optional<Object> result = sessionManager.findById(unknownId);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("세션을 삭제(Invalidate)하면 더 이상 조회되지 않아야 한다")
    void clearSession() {
        // given
        String userPayload = "UserToDelete";
        String sessionId = sessionManager.createSession(userPayload);
        assertTrue(sessionManager.findById(sessionId).isPresent());

        // when
        sessionManager.clearSession(sessionId);

        // then
        Optional<Object> result = sessionManager.findById(sessionId);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("서로 다른 사용자는 서로 다른 세션 ID를 가져야 한다")
    void createDistinctSessions() {
        // given
        String user1 = "UserA";
        String user2 = "UserB";

        // when
        String sid1 = sessionManager.createSession(user1);
        String sid2 = sessionManager.createSession(user2);

        // then
        assertNotEquals(sid1, sid2);
        assertEquals(user1, sessionManager.findById(sid1).get());
        assertEquals(user2, sessionManager.findById(sid2).get());
    }
}
