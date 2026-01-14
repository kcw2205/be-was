package dao.impl.h2;

import dao.UserDAO;
import db.H2DatabaseConfig;
import db.JDBCConnectionManager;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class UserDAOH2 extends H2DAO<User, String> implements UserDAO {

    private static final String TABLE_NAME = "USERS";
    private static final String CREATE_SQL =
        "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            "userId VARCHAR(50) PRIMARY KEY, " +
            "password VARCHAR(100) NOT NULL, " +
            "name VARCHAR(50) NOT NULL, " +
            "email VARCHAR(100) NOT NULL, " +
            "profileImagePath VARCHAR(255)" +
            ")";

    public UserDAOH2(H2DatabaseConfig h2DatabaseConfig, JDBCConnectionManager connectionManager) {
        super(h2DatabaseConfig, connectionManager, TABLE_NAME, CREATE_SQL);
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO USERS (userId, password, name, email, profileImagePath) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getProfileImagePath());

            pstmt.executeUpdate();

            // User는 보통 입력받은 userId가 PK이므로 저장된 유저 객체를 그대로 반환하거나
            // DB에서 다시 조회해서 반환하는 방식을 사용합니다.
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("사용자 추가 중 오류 발생", e);
        }
    }

    @Override
    public Optional<User> findById(String userId) {
        String sql = "SELECT * FROM USERS WHERE userId = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("사용자 조회 중 오류 발생", e);
        }
        return Optional.empty();
    }

    @Override
    public Collection<User> findAll() {
        String sql = "SELECT * FROM USERS";
        List<User> users = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("전체 사용자 목록 조회 중 오류 발생", e);
        }
        return users;
    }

    @Override
    public Optional<User> findByNickname(String name) {
        String sql = "SELECT * FROM USERS WHERE name = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("닉네임(이름)으로 조회 중 오류 발생", e);
        }
        return Optional.empty();
    }

    // ResultSet을 User 객체로 변환하는 헬퍼 메서드
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User(
            rs.getString("userId"),
            rs.getString("password"),
            rs.getString("name"),
            rs.getString("email")
        );
        // 기본 이미지 외의 값이 저장되어 있을 수 있으므로 업데이트
        user.updateProfileImage(rs.getString("profileImagePath"));
        return user;
    }
}
