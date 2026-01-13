package com.iabdinur.rowmapper;

import com.github.javafaker.Faker;
import com.iabdinur.model.VerificationCode;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VerificationCodeRowMapperTest {

    private Faker FAKER = new Faker();

    private VerificationCode createTestVerificationCode() {
        Long id = FAKER.random().nextLong();
        String email = FAKER.internet().emailAddress();
        String hashedCode = "$2a$10$JWw/S/0M1sZ4TbXioV/lv.JA6lO.aaaBP0qFl3asEseEgMITP0DK6";
        LocalDateTime createdAt = FAKER.date().past(10, TimeUnit.MINUTES).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime expiresAt = createdAt.plusMinutes(10);
        Integer attempts = FAKER.random().nextInt(0, 5);
        Boolean isUsed = false;

        VerificationCode code = new VerificationCode();
        code.setId(id);
        code.setEmail(email);
        code.setHashedCode(hashedCode);
        code.setCreatedAt(createdAt);
        code.setExpiresAt(expiresAt);
        code.setAttempts(attempts);
        code.setIsUsed(isUsed);
        return code;
    }

    @Test
    void itShouldMapRow() throws SQLException {
        // Given
        VerificationCode code = createTestVerificationCode();
        Long expectedId = code.getId();
        String expectedEmail = code.getEmail();
        String expectedHashedCode = code.getHashedCode();
        LocalDateTime expectedCreatedAt = code.getCreatedAt();
        LocalDateTime expectedExpiresAt = code.getExpiresAt();
        Integer expectedAttempts = code.getAttempts();
        Boolean expectedIsUsed = code.getIsUsed();

        VerificationCodeRowMapper rowMapper = new VerificationCodeRowMapper();
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getLong("id")).thenReturn(expectedId);
        when(resultSet.getString("email")).thenReturn(expectedEmail);
        when(resultSet.getString("hashed_code")).thenReturn(expectedHashedCode);
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(expectedCreatedAt));
        when(resultSet.getTimestamp("expires_at")).thenReturn(Timestamp.valueOf(expectedExpiresAt));
        when(resultSet.getInt("attempts")).thenReturn(expectedAttempts);
        when(resultSet.getBoolean("is_used")).thenReturn(expectedIsUsed);

        // When
        VerificationCode actual = rowMapper.mapRow(resultSet, 1);

        // Then
        assertThat(actual.getId()).isEqualTo(expectedId);
        assertThat(actual.getEmail()).isEqualTo(expectedEmail);
        assertThat(actual.getHashedCode()).isEqualTo(expectedHashedCode);
        assertThat(actual.getCreatedAt()).isEqualTo(expectedCreatedAt);
        assertThat(actual.getExpiresAt()).isEqualTo(expectedExpiresAt);
        assertThat(actual.getAttempts()).isEqualTo(expectedAttempts);
        assertThat(actual.getIsUsed()).isEqualTo(expectedIsUsed);
    }
}
