package com.iabdinur.repository;

import com.iabdinur.dao.TagDao;
import com.iabdinur.model.Tag;
import com.iabdinur.rowmapper.TagRowMapper;
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
public class TagJDBCDataAccessService implements TagDao {

    private final JdbcTemplate jdbcTemplate;
    private final TagRowMapper tagRowMapper;

    public TagJDBCDataAccessService(JdbcTemplate jdbcTemplate,
                                    TagRowMapper tagRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.tagRowMapper = tagRowMapper;
    }

    @Override
    public List<Tag> selectAllTags() {
        var sql = """
                SELECT id, name, slug, description, posts_count, created_at, updated_at
                FROM tags
                ORDER BY name
                """;
        return jdbcTemplate.query(sql, tagRowMapper);
    }

    @Override
    public Optional<Tag> selectTagById(Long tagId) {
        var sql = """
                SELECT id, name, slug, description, posts_count, created_at, updated_at
                FROM tags
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, tagRowMapper, tagId)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Tag> selectTagBySlug(String slug) {
        var sql = """
                SELECT id, name, slug, description, posts_count, created_at, updated_at
                FROM tags
                WHERE slug = ?
                """;
        return jdbcTemplate.query(sql, tagRowMapper, slug)
                .stream()
                .findFirst();
    }

    @Override
    public void insertTag(Tag tag) {
        var sql = """
                INSERT INTO tags(name, slug, description, posts_count, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, tag.getName());
            ps.setString(2, tag.getSlug());
            ps.setString(3, tag.getDescription());
            ps.setInt(4, tag.getPostsCount() != null ? tag.getPostsCount() : 0);
            ps.setTimestamp(5, Timestamp.valueOf(tag.getCreatedAt()));
            ps.setTimestamp(6, Timestamp.valueOf(tag.getUpdatedAt()));
            return ps;
        }, keyHolder);
        
        Long id = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        tag.setId(id);
    }

    @Override
    public boolean existsTagWithSlug(String slug) {
        var sql = """
                SELECT count(id)
                FROM tags
                WHERE slug = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, slug);
        return count != null && count > 0;
    }

    @Override
    public boolean existsTagWithName(String name) {
        var sql = """
                SELECT count(id)
                FROM tags
                WHERE name = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != null && count > 0;
    }

    @Override
    public boolean existsTagById(Long tagId) {
        var sql = """
                SELECT count(id)
                FROM tags
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tagId);
        return count != null && count > 0;
    }

    @Override
    public void deleteTagById(Long tagId) {
        var sql = """
                DELETE
                FROM tags
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, tagId);
    }

    @Override
    public void updateTag(Tag update) {
        if (update.getName() != null) {
            String sql = "UPDATE tags SET name = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getName(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
        if (update.getSlug() != null) {
            String sql = "UPDATE tags SET slug = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getSlug(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
        if (update.getDescription() != null) {
            String sql = "UPDATE tags SET description = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getDescription(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
        if (update.getPostsCount() != null) {
            String sql = "UPDATE tags SET posts_count = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getPostsCount(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
    }
}
