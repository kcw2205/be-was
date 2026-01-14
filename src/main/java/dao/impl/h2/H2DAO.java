package dao.impl.h2;

import dao.DataAccessObject;
import db.H2DatabaseConfig;
import db.JDBCConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class H2DAO<T, K> implements DataAccessObject<T, K> {

    private final JDBCConnectionManager connectionManager;
    private final H2DatabaseConfig h2DatabaseConfig;

    public H2DAO(
        H2DatabaseConfig h2DatabaseConfig,
        JDBCConnectionManager connectionManager,
        String tableName,
        String entityCreateStatement) {

        this.connectionManager = connectionManager;
        this.h2DatabaseConfig = h2DatabaseConfig;

        this.createOrConnectTable(tableName, entityCreateStatement);
    }

    private void createOrConnectTable(
        String tableName,
        String entityCreateStatement) {
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement()) {

            switch (this.h2DatabaseConfig.getH2DAOTableCreatePolicy()) {
                case ALWAYS:
                    stmt.execute("DROP TABLE IF EXISTS " + tableName);
                case NOT_EXISTS:
                    stmt.execute(entityCreateStatement);
                    break;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create H2 Database Table ", e);
        }
    }

    protected Connection getConnection() throws SQLException {
        return this.connectionManager.getConnection();
    }
}
