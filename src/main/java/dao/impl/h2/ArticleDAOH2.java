package dao.impl.h2;

import dao.ArticleDAO;
import db.H2DatabaseConfig;
import db.JDBCConnectionManager;
import model.Article;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ArticleDAOH2 extends H2DAO<Article, Long> implements ArticleDAO {

    private static final String TABLE_NAME = "ARTICLES";

    private static final String CREATE_SQL =
        "CREATE TABLE IF NOT EXISTS ARTICLES (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "author_id VARCHAR(255) NOT NULL, " +
            "image_path VARCHAR(500), " +
            "content TEXT, " +
            "like_count BIGINT DEFAULT 0" +
            ")";

    public ArticleDAOH2(H2DatabaseConfig h2DatabaseConfig, JDBCConnectionManager connectionManager) {
        super(h2DatabaseConfig, connectionManager, TABLE_NAME, CREATE_SQL);
    }

    @Override
    public Article save(Article data) {
        String sql = "INSERT INTO ARTICLES (author_id, image_path, content, like_count) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, data.getAuthorId());
            pstmt.setString(2, data.getImagePath());
            pstmt.setString(3, data.getContent());
            pstmt.setLong(4, data.getLikeCount());

            pstmt.executeUpdate();

            // 생성된 ID 가져오기
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    return new Article(
                        generatedId,
                        data.getAuthorId(),
                        data.getImagePath(),
                        data.getContent(),
                        data.getLikeCount()
                    );
                }
            }
            throw new SQLException("Article 생성 실패: ID를 가져올 수 없습니다.");
        } catch (SQLException e) {
            throw new RuntimeException("Error adding article", e);
        }
    }

    @Override
    public Optional<Article> findById(Long key) {
        String sql = "SELECT * FROM ARTICLES WHERE id = ?";
        return executeQuerySingle(sql, key);
    }

    @Override
    public Collection<Article> findAll() {
        String sql = "SELECT * FROM ARTICLES ORDER BY id DESC";
        return executeQueryList(sql);
    }

    @Override
    public Optional<Article> findPreviousById(long id) {
        String sql = "SELECT * FROM ARTICLES WHERE id < ? ORDER BY id DESC LIMIT 1";
        return executeQuerySingle(sql, id);
    }

    @Override
    public Optional<Article> findNextById(long id) {
        String sql = "SELECT * FROM ARTICLES WHERE id > ? ORDER BY id ASC LIMIT 1";
        return executeQuerySingle(sql, id);
    }

    @Override
    public Optional<Article> findFirstOrderByIdDesc() {
        String sql = "SELECT * FROM ARTICLES ORDER BY id DESC LIMIT 1";
        return executeQuery(sql);
    }

    private Optional<Article> executeQuery(String sql) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToArticle(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in executeQuerySingle", e);
        }
        return Optional.empty();
    }

    private Optional<Article> executeQuerySingle(String sql, Object param) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, param);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToArticle(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in executeQuerySingle", e);
        }
        return Optional.empty();
    }

    private List<Article> executeQueryList(String sql) {
        List<Article> articles = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                articles.add(mapRowToArticle(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in executeQueryList", e);
        }
        return articles;
    }

    private Article mapRowToArticle(ResultSet rs) throws SQLException {
        return new Article(
            rs.getLong("id"),
            rs.getString("author_id"),
            rs.getString("image_path"),
            rs.getString("content"),
            rs.getLong("like_count")
        );
    }
}