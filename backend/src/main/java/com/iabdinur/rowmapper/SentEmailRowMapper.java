package com.iabdinur.rowmapper;

import com.iabdinur.model.SentEmail;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class SentEmailRowMapper implements RowMapper<SentEmail> {

    @Override
    public SentEmail mapRow(ResultSet rs, int rowNum) throws SQLException {
        SentEmail sentEmail = new SentEmail();
        sentEmail.setId(rs.getLong("id"));
        sentEmail.setRecipientEmail(rs.getString("recipient_email"));
        sentEmail.setSubject(rs.getString("subject"));
        sentEmail.setEmailType(rs.getString("email_type"));
        sentEmail.setSesMessageId(rs.getString("ses_message_id"));
        sentEmail.setStatus(rs.getString("status"));
        
        if (rs.getTimestamp("sent_at") != null) {
            sentEmail.setSentAt(rs.getTimestamp("sent_at").toLocalDateTime());
        }
        
        if (rs.getTimestamp("delivered_at") != null) {
            sentEmail.setDeliveredAt(rs.getTimestamp("delivered_at").toLocalDateTime());
        }
        
        sentEmail.setErrorMessage(rs.getString("error_message"));
        
        return sentEmail;
    }
}
