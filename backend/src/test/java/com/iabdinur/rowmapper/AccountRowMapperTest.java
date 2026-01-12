package com.iabdinur.rowmapper;

import com.github.javafaker.Faker;
import com.iabdinur.model.Account;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountRowMapperTest {

    private Faker FAKER = new Faker();

    private Account createTestAccount() {
        Long accountId = FAKER.random().nextLong();
        String username = FAKER.name().username();
        String password = FAKER.internet().password();
        LocalDateTime createdAt = FAKER.date().past(365, TimeUnit.DAYS).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updatedAt = FAKER.date().past(30, TimeUnit.DAYS).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        return new Account(accountId, username, password, createdAt, updatedAt);
    }

    @Test
    void itShouldMapRow() throws SQLException {
        // Given
        Account account = createTestAccount();
        Long expectedId = account.getId();
        String expectedUsername = account.getUsername();
        String expectedPassword = account.getPassword();
        LocalDateTime expectedCreatedAt = account.getCreatedAt();
        LocalDateTime expectedUpdatedAt = account.getUpdatedAt();

        AccountRowMapper accountRowMapper = new AccountRowMapper();
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getLong("id")).thenReturn(expectedId);
        when(resultSet.getString("username")).thenReturn(expectedUsername);
        when(resultSet.getString("password")).thenReturn(expectedPassword);
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(expectedCreatedAt));
        when(resultSet.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf(expectedUpdatedAt));

        // When
        Account actual = accountRowMapper.mapRow(resultSet, 1);

        // Then
        Account expected = new Account(expectedId, expectedUsername, expectedPassword, expectedCreatedAt, expectedUpdatedAt);
        assertThat(actual).isEqualTo(expected);
    }
}
