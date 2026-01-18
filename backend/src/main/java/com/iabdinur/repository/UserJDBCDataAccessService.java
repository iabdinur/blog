package com.iabdinur.repository;

import com.iabdinur.dao.UserDao;
import com.iabdinur.model.User;
import com.iabdinur.rowmapper.UserRowMapper;
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
public class UserJDBCDataAccessService implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    public UserJDBCDataAccessService(JdbcTemplate jdbcTemplate,
                                       UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public List<User> selectAllUsers() {
        var sql = """
                SELECT id, name, email, password, user_type, created_at, updated_at, profile_image_id
                FROM users
                LIMIT 1000
                """;
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> selectUserById(Long userId) {
        var sql = """
                SELECT id, name, email, password, user_type, created_at, updated_at, profile_image_id
                FROM users
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, userRowMapper, userId)
                .stream()
                .findFirst();
    }

    @Override
    public void insertUser(User user) {
        var sql = """
                INSERT INTO users(name, email, password, user_type, created_at, updated_at, profile_image_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getUserType() != null ? user.getUserType().name() : "REA");
            ps.setTimestamp(5, Timestamp.valueOf(user.getCreatedAt()));
            ps.setTimestamp(6, Timestamp.valueOf(user.getUpdatedAt()));
            ps.setString(7, user.getProfileImageId());
            return ps;
        }, keyHolder);
        
        Long id = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        user.setId(id);
    }

    @Override
    public boolean existsUserWithEmail(String email) {
        var sql = """
                SELECT count(id)
                FROM users
                WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsUserById(Long userId) {
        var sql = """
                SELECT count(id)
                FROM users
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    @Override
    public void deleteUserById(Long userId) {
        var sql = """
                DELETE
                FROM users
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, userId);
    }

    @Override
    public void updateUser(User update) {
        if (update.getName() != null) {
            String sql = "UPDATE users SET name = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getName(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
        if (update.getEmail() != null) {
            String sql = "UPDATE users SET email = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getEmail(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
        if (update.getPassword() != null) {
            String sql = "UPDATE users SET password = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getPassword(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
        if (update.getProfileImageId() != null) {
            String sql = "UPDATE users SET profile_image_id = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getProfileImageId(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
        if (update.getUserType() != null) {
            String sql = "UPDATE users SET user_type = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getUserType().name(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
    }

    @Override
    public Optional<User> selectUserByEmail(String email) {
        var sql = """
                SELECT id, name, email, password, user_type, created_at, updated_at, profile_image_id
                FROM users
                WHERE email = ?
                """;
        return jdbcTemplate.query(sql, userRowMapper, email)
                .stream()
                .findFirst();
    }
}
