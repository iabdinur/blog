package com.iabdinur.rowmapper;

import com.iabdinur.model.VerificationCode;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class VerificationCodeRowMapper implements RowMapper<VerificationCode> {
    
    @Override
    public VerificationCode mapRow(ResultSet rs, int rowNum) throws SQLException {
        VerificationCode code = new VerificationCode();
        code.setId(rs.getLong("id"));
        code.setEmail(rs.getString("email"));
        code.setHashedCode(rs.getString("hashed_code"));
        code.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        code.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
        code.setAttempts(rs.getInt("attempts"));
        code.setIsUsed(rs.getBoolean("is_used"));
        return code;
    }
}
