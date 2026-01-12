package com.iabdinur.repository;

import com.iabdinur.dao.CommentDao;
import com.iabdinur.model.Comment;
import com.iabdinur.rowmapper.CommentRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class CommentJDBCDataAccessService implements CommentDao {

    private final JdbcTemplate jdbcTemplate;
    private final CommentRowMapper commentRowMapper;

    public CommentJDBCDataAccessService(JdbcTemplate jdbcTemplate,
                                         CommentRowMapper commentRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.commentRowMapper = commentRowMapper;
    }

    @Override
    public List<Comment> selectAllComments() {
        var sql = """
                SELECT id, post_id, author_id, content, parent_id, likes, created_at, updated_at
                FROM comments
                ORDER BY created_at DESC
                """;
        return jdbcTemplate.query(sql, commentRowMapper);
    }

    @Override
    public List<Comment> selectCommentsByPostId(Long postId) {
        var sql = """
                SELECT id, post_id, author_id, content, parent_id, likes, created_at, updated_at
                FROM comments
                WHERE post_id = ?
                ORDER BY created_at DESC
                """;
        return jdbcTemplate.query(sql, commentRowMapper, postId);
    }

    @Override
    public Optional<Comment> selectCommentById(Long commentId) {
        var sql = """
                SELECT id, post_id, author_id, content, parent_id, likes, created_at, updated_at
                FROM comments
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, commentRowMapper, commentId)
                .stream()
                .findFirst();
    }

    @Override
    public void insertComment(Comment comment) {
        var sql = """
                INSERT INTO comments(post_id, author_id, content, parent_id, likes, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, comment.getPost().getId());
            ps.setLong(2, comment.getAuthor().getId());
            ps.setString(3, comment.getContent());
            ps.setObject(4, comment.getParent() != null ? comment.getParent().getId() : null);
            ps.setInt(5, comment.getLikes() != null ? comment.getLikes() : 0);
            ps.setTimestamp(6, Timestamp.valueOf(comment.getCreatedAt()));
            ps.setTimestamp(7, Timestamp.valueOf(comment.getUpdatedAt()));
            return ps;
        }, keyHolder);
        
        Long id = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        comment.setId(id);
    }

    @Override
    public boolean existsCommentById(Long commentId) {
        var sql = """
                SELECT count(id)
                FROM comments
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, commentId);
        return count != null && count > 0;
    }

    @Override
    public void deleteCommentById(Long commentId) {
        var sql = """
                DELETE
                FROM comments
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, commentId);
    }

    @Override
    public void updateComment(Comment update) {
        if (update.getContent() != null) {
            String sql = "UPDATE comments SET content = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getContent(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
    }

    @Override
    public void incrementLikes(Long commentId) {
        var sql = """
                UPDATE comments
                SET likes = likes + 1
                WHERE id = ?
                """;
        int updated = jdbcTemplate.update(sql, commentId);
        if (updated == 0) {
            throw new IllegalArgumentException("Comment not found with id: " + commentId);
        }
    }
}
