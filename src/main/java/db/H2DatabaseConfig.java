package db;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class H2DatabaseConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(H2DatabaseConfig.class);
    private static final String H2_CONSOLE_PORT = "8082";
    private static final H2TableCreatePolicy H_2_TABLE_CREATE_POLICY = H2TableCreatePolicy.NOT_EXISTS;

    public H2DatabaseConfig() {
        this.startH2Console();
    }

    public void startH2Console() {
        try {
            Server webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", H2_CONSOLE_PORT).start();
            LOGGER.info("Started H2 Console Server: {}", webServer.getURL());
        } catch (SQLException e) {
            LOGGER.error("H2 Console 서버를 여는 도중 에러가 발생했습니다. ", e);
            throw new RuntimeException(e);
        }
    }

    public H2TableCreatePolicy getH2DAOTableCreatePolicy() {
        return H_2_TABLE_CREATE_POLICY;
    }
}