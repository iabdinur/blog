package com.iabdinur.repository;

import com.iabdinur.dao.SentEmailDao;
import com.iabdinur.model.SentEmail;
import com.iabdinur.rowmapper.SentEmailRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SentEmailJDBCDataAccessService implements SentEmailDao {

    private final JdbcTemplate jdbcTemplate;
    private final SentEmailRowMapper rowMapper;

    public SentEmailJDBCDataAccessService(
            JdbcTemplate jdbcTemplate,
            SentEmailRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public void insertSentEmail(SentEmail sentEmail) {
        String sql = """
            INSERT INTO sent_emails (recipient_email, subject, email_type, ses_message_id, status, sent_at, error_message)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        jdbcTemplate.update(
            sql,
            sentEmail.getRecipientEmail(),
            sentEmail.getSubject(),
            sentEmail.getEmailType(),
            sentEmail.getSesMessageId(),
            sentEmail.getStatus(),
            sentEmail.getSentAt(),
            sentEmail.getErrorMessage()
        );
    }
}
