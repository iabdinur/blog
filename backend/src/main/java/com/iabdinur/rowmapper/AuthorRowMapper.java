package com.iabdinur.rowmapper;

import com.iabdinur.model.Author;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class AuthorRowMapper implements RowMapper<Author> {
    @Override
    public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
        java.sql.Timestamp joinedAtTimestamp = rs.getTimestamp("joined_at");
        java.sql.Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        java.sql.Timestamp updatedAtTimestamp = rs.getTimestamp("updated_at");
        
        Author author = new Author(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("username"),
                rs.getString("email"),
                joinedAtTimestamp != null ? joinedAtTimestamp.toLocalDateTime() : LocalDateTime.now(),
                createdAtTimestamp != null ? createdAtTimestamp.toLocalDateTime() : LocalDateTime.now(),
                updatedAtTimestamp != null ? updatedAtTimestamp.toLocalDateTime() : LocalDateTime.now()
        );
        author.setBio(rs.getString("bio"));
        author.setAvatar(rs.getString("avatar"));
        author.setCoverImage(rs.getString("cover_image"));
        author.setLocation(rs.getString("location"));
        author.setWebsite(rs.getString("website"));
        author.setGithub(rs.getString("github"));
        author.setLinkedin(rs.getString("linkedin"));
        author.setFollowersCount(rs.getInt("followers_count"));
        author.setPostsCount(rs.getInt("posts_count"));
        return author;
    }
}
