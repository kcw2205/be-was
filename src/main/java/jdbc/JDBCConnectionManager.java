package jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC의 Connection을 관리해주는 객체
 * <br>
 * 추후에 아래처럼 커넥션 풀을 관리해주는 간단한 작은 CP 를 만들어볼 수도 있을 것이다.
 * <br>
 * <pre>
 * {@code
 * private static final int CONNECTION_POOL_COUNT = 8;
 * List<Connection> connections;
 * }
 * </pre>
 * @see Connection
 */
public class JDBCConnectionManager {
    // TODO: resource 에서 프로퍼티 파싱하게 만들기 (언젠가?)
    private static final Logger LOGGER = LoggerFactory.getLogger(JDBCConnectionManager.class);
    private static final String CONNECT_URL = "jdbc:h2:mem:db";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public JDBCConnectionManager() {
        this.start();
    }

    public void start() {
        try {
            Class.forName("org.h2.Driver");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONNECT_URL, USER, PASSWORD);
    }
}
