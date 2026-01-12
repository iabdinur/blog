package com.iabdinur.repository;

import com.iabdinur.dao.AuthorDao;
import com.iabdinur.model.Author;
import com.iabdinur.rowmapper.AuthorRowMapper;
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
public class AuthorJDBCDataAccessService implements AuthorDao {

    private final JdbcTemplate jdbcTemplate;
    private final AuthorRowMapper authorRowMapper;

    public AuthorJDBCDataAccessService(JdbcTemplate jdbcTemplate,
                                        AuthorRowMapper authorRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.authorRowMapper = authorRowMapper;
    }

    @Override
    public List<Author> selectAllAuthors() {
        var sql = """
                SELECT id, name, username, email, bio, avatar, cover_image, location, website,
                       twitter, github, linkedin, followers_count, following_count, posts_count,
                       joined_at, created_at, updated_at
                FROM authors
                ORDER BY name
                """;
        return jdbcTemplate.query(sql, authorRowMapper);
    }

    @Override
    public Optional<Author> selectAuthorById(Long authorId) {
        var sql = """
                SELECT id, name, username, email, bio, avatar, cover_image, location, website,
                       twitter, github, linkedin, followers_count, following_count, posts_count,
                       joined_at, created_at, updated_at
                FROM authors
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, authorRowMapper, authorId)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Author> selectAuthorByUsername(String username) {
        var sql = """
                SELECT id, name, username, email, bio, avatar, cover_image, location, website,
                       twitter, github, linkedin, followers_count, following_count, posts_count,
                       joined_at, created_at, updated_at
                FROM authors
                WHERE username = ?
                """;
        return jdbcTemplate.query(sql, authorRowMapper, username)
                .stream()
                .findFirst();
    }

    @Override
    public void insertAuthor(Author author) {
        var sql = """
                INSERT INTO authors(name, username, email, bio, avatar, cover_image, location, website,
                                   twitter, github, linkedin, followers_count, following_count, posts_count,
                                   joined_at, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, author.getName());
            ps.setString(2, author.getUsername());
            ps.setString(3, author.getEmail());
            ps.setString(4, author.getBio());
            ps.setString(5, author.getAvatar());
            ps.setString(6, author.getCoverImage());
            ps.setString(7, author.getLocation());
            ps.setString(8, author.getWebsite());
            ps.setString(9, author.getTwitter());
            ps.setString(10, author.getGithub());
            ps.setString(11, author.getLinkedin());
            ps.setInt(12, author.getFollowersCount() != null ? author.getFollowersCount() : 0);
            ps.setInt(13, author.getFollowingCount() != null ? author.getFollowingCount() : 0);
            ps.setInt(14, author.getPostsCount() != null ? author.getPostsCount() : 0);
            ps.setTimestamp(15, Timestamp.valueOf(author.getJoinedAt()));
            ps.setTimestamp(16, Timestamp.valueOf(author.getCreatedAt()));
            ps.setTimestamp(17, Timestamp.valueOf(author.getUpdatedAt()));
            return ps;
        }, keyHolder);
        
        Long id = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        author.setId(id);
    }

    @Override
    public boolean existsAuthorWithUsername(String username) {
        var sql = """
                SELECT count(id)
                FROM authors
                WHERE username = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    @Override
    public boolean existsAuthorWithEmail(String email) {
        var sql = """
                SELECT count(id)
                FROM authors
                WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsAuthorById(Long authorId) {
        var sql = """
                SELECT count(id)
                FROM authors
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, authorId);
        return count != null && count > 0;
    }

    @Override
    public void deleteAuthorById(Long authorId) {
        var sql = """
                DELETE
                FROM authors
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, authorId);
    }

    @Override
    public void updateAuthor(Author update) {
        if (update.getName() != null) {
            String sql = "UPDATE authors SET name = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getName(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
        if (update.getEmail() != null) {
            String sql = "UPDATE authors SET email = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getEmail(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
        if (update.getBio() != null) {
            String sql = "UPDATE authors SET bio = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getBio(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
        // Add more fields as needed
    }
}
