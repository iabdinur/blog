package com.iabdinur.rowmapper;

import com.iabdinur.model.Post;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class PostRowMapper implements RowMapper<Post> {
    @Override
    public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setSlug(rs.getString("slug"));
        post.setContent(rs.getString("content"));
        post.setExcerpt(rs.getString("excerpt"));
        post.setCoverImage(rs.getString("cover_image"));
        
        Timestamp publishedAt = rs.getTimestamp("published_at");
        if (publishedAt != null) {
            post.setPublishedAt(publishedAt.toLocalDateTime());
        }
        
        Timestamp scheduledAt = rs.getTimestamp("scheduled_at");
        if (scheduledAt != null) {
            post.setScheduledAt(scheduledAt.toLocalDateTime());
        }
        
        post.setIsPublished(rs.getBoolean("is_published"));
        post.setViews(rs.getLong("views"));
        post.setLikes(rs.getLong("likes"));
        post.setCommentsCount(rs.getInt("comments_count"));
        
        int readingTime = rs.getInt("reading_time");
        if (!rs.wasNull()) {
            post.setReadingTime(readingTime);
        }
        
        post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        post.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        // Note: author and tags will be loaded separately via joins or additional queries
        return post;
    }
}
