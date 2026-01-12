package com.iabdinur.rowmapper;

import com.iabdinur.model.Comment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class CommentRowMapper implements RowMapper<Comment> {
    @Override
    public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setContent(rs.getString("content"));
        comment.setLikes(rs.getInt("likes"));
        comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        comment.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        // Note: post, author, and parent will be loaded separately via joins or additional queries
        return comment;
    }
}
