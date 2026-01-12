package com.iabdinur.rowmapper;

import com.iabdinur.model.Tag;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class TagRowMapper implements RowMapper<Tag> {
    @Override
    public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
        Tag tag = new Tag(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("slug"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        );
        tag.setDescription(rs.getString("description"));
        tag.setPostsCount(rs.getInt("posts_count"));
        return tag;
    }
}
