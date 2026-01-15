package dao.impl.h2;

import dao.CommentDAO;
import db.H2DatabaseConfig;
import db.JDBCConnectionManager;
import model.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CommentDAOH2 extends H2DAO<Comment, Long> implements CommentDAO {

    private static final String TABLE_NAME = "COMMENTS";

    // 테이블 생성 SQL: article_id 등 외래키 관계가 있다면 나중에 추가 가능
    private static final String CREATE_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "author_id VARCHAR(255) NOT NULL, " +
            "article_id BIGINT NOT NULL, " +
            "comment_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "content TEXT NOT NULL" +
            ")";

    public CommentDAOH2(H2DatabaseConfig h2DatabaseConfig, JDBCConnectionManager connectionManager) {
        super(h2DatabaseConfig, connectionManager, TABLE_NAME, CREATE_TABLE_SQL);
    }

    @Override
    public Comment save(Comment data) {
        String sql = "INSERT INTO comments (author_id, article_id, comment_at, content) VALUES (?, ?, ?, ?)";
        LocalDateTime at = (data.getCommentAt() != null) ? data.getCommentAt() : LocalDateTime.now();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, data.getAuthorId());
            pstmt.setLong(2, data.getArticleId());
            pstmt.setTimestamp(3, Timestamp.valueOf(at));
            pstmt.setString(4, data.getContent());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Comment(
                        generatedKeys.getLong(1),
                        data.getAuthorId(),
                        data.getArticleId(),
                        at,
                        data.getContent()
                    );
                }
            }
            throw new SQLException("Comment 생성 실패: ID를 가져올 수 없습니다.");
        } catch (SQLException e) {
            throw new RuntimeException("댓글 저장 중 오류 발생", e);
        }
    }

    @Override
    public Optional<Comment> findById(Long key) {
        String sql = "SELECT * FROM comments WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToComment(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ID로 댓글 조회 중 오류 발생", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Comment> findByAuthorId(long authorId) {
        String sql = "SELECT * FROM comments WHERE author_id = ? ORDER BY comment_at DESC";
        return executeQueryList(sql, String.valueOf(authorId));
    }

    @Override
    public List<Comment> findByArticleId(long articleId) {
        String sql = "SELECT * FROM comments WHERE article_id = ? ORDER BY comment_at ASC";
        return executeQueryList(sql, articleId);
    }

    @Override
    public Collection<Comment> findAll() {
        String sql = "SELECT * FROM comments ORDER BY comment_at DESC";
        return executeQueryList(sql);
    }

    private Comment mapRowToComment(ResultSet rs) throws SQLException {
        return new Comment(
            rs.getLong("id"),
            rs.getString("author_id"),
            rs.getLong("article_id"),
            rs.getTimestamp("comment_at").toLocalDateTime(),
            rs.getString("content")
        );
    }

    private List<Comment> executeQueryList(String sql, Object... params) {
        List<Comment> comments = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapRowToComment(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("댓글 목록 조회 중 오류 발생", e);
        }
        return comments;
    }
}