package com.iabdinur.repository;

import com.iabdinur.dao.AccountDao;
import com.iabdinur.model.Account;
import com.iabdinur.rowmapper.AccountRowMapper;
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
public class AccountJDBCDataAccessService implements AccountDao {

    private final JdbcTemplate jdbcTemplate;
    private final AccountRowMapper accountRowMapper;

    public AccountJDBCDataAccessService(JdbcTemplate jdbcTemplate,
                                       AccountRowMapper accountRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountRowMapper = accountRowMapper;
    }

    @Override
    public List<Account> selectAllAccounts() {
        var sql = """
                SELECT id, username, password, created_at, updated_at
                FROM accounts
                LIMIT 1000
                """;
        return jdbcTemplate.query(sql, accountRowMapper);
    }

    @Override
    public Optional<Account> selectAccountById(Long accountId) {
        var sql = """
                SELECT id, username, password, created_at, updated_at
                FROM accounts
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, accountRowMapper, accountId)
                .stream()
                .findFirst();
    }

    @Override
    public void insertAccount(Account account) {
        var sql = """
                INSERT INTO accounts(username, password, created_at, updated_at)
                VALUES (?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.setTimestamp(3, Timestamp.valueOf(account.getCreatedAt()));
            ps.setTimestamp(4, Timestamp.valueOf(account.getUpdatedAt()));
            return ps;
        }, keyHolder);
        
        Long id = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        account.setId(id);
    }

    @Override
    public boolean existsAccountWithUsername(String username) {
        var sql = """
                SELECT count(id)
                FROM accounts
                WHERE username = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    @Override
    public boolean existsAccountById(Long accountId) {
        var sql = """
                SELECT count(id)
                FROM accounts
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, accountId);
        return count != null && count > 0;
    }

    @Override
    public void deleteAccountById(Long accountId) {
        var sql = """
                DELETE
                FROM accounts
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, accountId);
    }

    @Override
    public void updateAccount(Account update) {
        if (update.getUsername() != null) {
            String sql = "UPDATE accounts SET username = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getUsername(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
        if (update.getPassword() != null) {
            String sql = "UPDATE accounts SET password = ?, updated_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    update.getPassword(),
                    Timestamp.valueOf(LocalDateTime.now()),
                    update.getId());
        }
    }

    @Override
    public Optional<Account> selectAccountByUsername(String username) {
        var sql = """
                SELECT id, username, password, created_at, updated_at
                FROM accounts
                WHERE username = ?
                """;
        return jdbcTemplate.query(sql, accountRowMapper, username)
                .stream()
                .findFirst();
    }
}
