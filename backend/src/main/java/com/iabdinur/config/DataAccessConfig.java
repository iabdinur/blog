package com.iabdinur.config;

import com.iabdinur.dao.SentEmailDao;
import com.iabdinur.dao.VerificationCodeDao;
import com.iabdinur.repository.SentEmailJDBCDataAccessService;
import com.iabdinur.repository.VerificationCodeJDBCDataAccessService;
import com.iabdinur.rowmapper.SentEmailRowMapper;
import com.iabdinur.rowmapper.VerificationCodeRowMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataAccessConfig {

    // VerificationCodeRowMapper is already @Component, so we inject it
    // Only register the DAO beans here
    @Bean
    public VerificationCodeDao verificationCodeDao(
            JdbcTemplate jdbcTemplate,
            VerificationCodeRowMapper rowMapper) {
        return new VerificationCodeJDBCDataAccessService(jdbcTemplate, rowMapper);
    }

    // SentEmailRowMapper is already @Component, so we inject it
    @Bean
    public SentEmailDao sentEmailDao(
            JdbcTemplate jdbcTemplate,
            SentEmailRowMapper rowMapper) {
        return new SentEmailJDBCDataAccessService(jdbcTemplate, rowMapper);
    }
}
