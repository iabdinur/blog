package com.iabdinur.repository;

import com.iabdinur.dao.VerificationCodeDao;
import com.iabdinur.model.VerificationCode;
import com.iabdinur.rowmapper.VerificationCodeRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class VerificationCodeJDBCDataAccessService implements VerificationCodeDao {
    
    private final JdbcTemplate jdbcTemplate;
    private final VerificationCodeRowMapper rowMapper;
    
    public VerificationCodeJDBCDataAccessService(
            JdbcTemplate jdbcTemplate,
            VerificationCodeRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }
    
    @Override
    public void insertVerificationCode(VerificationCode verificationCode) {
        String sql = """
            INSERT INTO verification_codes (email, hashed_code, created_at, expires_at, attempts, is_used)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        jdbcTemplate.update(
            sql,
            verificationCode.getEmail(),
            verificationCode.getHashedCode(),
            verificationCode.getCreatedAt(),
            verificationCode.getExpiresAt(),
            verificationCode.getAttempts(),
            verificationCode.getIsUsed()
        );
    }
    
    @Override
    public Optional<VerificationCode> findActiveCodeByEmail(String email) {
        String sql = """
            SELECT id, email, hashed_code, created_at, expires_at, attempts, is_used
            FROM verification_codes
            WHERE email = ? 
            AND is_used = FALSE 
            AND expires_at > CURRENT_TIMESTAMP
            ORDER BY created_at DESC
            LIMIT 1
            """;
        return jdbcTemplate.query(sql, rowMapper, email)
            .stream()
            .findFirst();
    }
    
    @Override
    public void updateVerificationCode(VerificationCode verificationCode) {
        String sql = """
            UPDATE verification_codes
            SET attempts = ?, is_used = ?
            WHERE id = ?
            """;
        jdbcTemplate.update(
            sql,
            verificationCode.getAttempts(),
            verificationCode.getIsUsed(),
            verificationCode.getId()
        );
    }
    
    @Override
    public void deleteExpiredCodes() {
        String sql = "DELETE FROM verification_codes WHERE expires_at < CURRENT_TIMESTAMP";
        jdbcTemplate.update(sql);
    }
    
    @Override
    public void invalidateCode(String email) {
        String sql = """
            UPDATE verification_codes
            SET is_used = TRUE
            WHERE email = ? AND is_used = FALSE
            """;
        jdbcTemplate.update(sql, email);
    }
    
    @Override
    public int countRecentCodesByEmail(String email, LocalDateTime since) {
        String sql = """
            SELECT COUNT(*)
            FROM verification_codes
            WHERE email = ? AND created_at >= ?
            """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email, since);
        return count != null ? count : 0;
    }
}
