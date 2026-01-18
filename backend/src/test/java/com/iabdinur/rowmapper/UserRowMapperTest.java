package com.iabdinur.rowmapper;

import com.github.javafaker.Faker;
import com.iabdinur.model.User;
import com.iabdinur.model.UserType;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserRowMapperTest {

    private Faker FAKER = new Faker();

    private User createTestUser() {
        Long userId = FAKER.random().nextLong();
        String name = FAKER.name().fullName();
        String email = FAKER.internet().emailAddress();
        String password = FAKER.internet().password();
        LocalDateTime createdAt = FAKER.date().past(365, TimeUnit.DAYS).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updatedAt = FAKER.date().past(30, TimeUnit.DAYS).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        String profileImageId = FAKER.internet().uuid();

        return new User(userId, name, email, password, UserType.REA, createdAt, updatedAt, profileImageId);
    }

    @Test
    void itShouldMapRow() throws SQLException {
        // Given
        User user = createTestUser();
        Long expectedId = user.getId();
        String expectedName = user.getName();
        String expectedEmail = user.getEmail();
        String expectedPassword = user.getPassword();
        LocalDateTime expectedCreatedAt = user.getCreatedAt();
        LocalDateTime expectedUpdatedAt = user.getUpdatedAt();
        String expectedProfileImageId = user.getProfileImageId();

        UserRowMapper userRowMapper = new UserRowMapper();
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getLong("id")).thenReturn(expectedId);
        when(resultSet.getString("name")).thenReturn(expectedName);
        when(resultSet.getString("email")).thenReturn(expectedEmail);
        when(resultSet.getString("password")).thenReturn(expectedPassword);
        when(resultSet.getString("user_type")).thenReturn("REA");
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(expectedCreatedAt));
        when(resultSet.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf(expectedUpdatedAt));
        when(resultSet.getString("profile_image_id")).thenReturn(expectedProfileImageId);

        // When
        User actual = userRowMapper.mapRow(resultSet, 1);

        // Then
        User expected = new User(expectedId, expectedName, expectedEmail, expectedPassword, UserType.REA, expectedCreatedAt, expectedUpdatedAt, expectedProfileImageId);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void itShouldMapRowWithNullProfileImageId() throws SQLException {
        // Given
        Long userId = FAKER.random().nextLong();
        String name = FAKER.name().fullName();
        String email = FAKER.internet().emailAddress();
        String password = FAKER.internet().password();
        LocalDateTime createdAt = FAKER.date().past(365, TimeUnit.DAYS).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updatedAt = FAKER.date().past(30, TimeUnit.DAYS).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        UserRowMapper userRowMapper = new UserRowMapper();
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getLong("id")).thenReturn(userId);
        when(resultSet.getString("name")).thenReturn(name);
        when(resultSet.getString("email")).thenReturn(email);
        when(resultSet.getString("password")).thenReturn(password);
        when(resultSet.getString("user_type")).thenReturn("REA");
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(createdAt));
        when(resultSet.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf(updatedAt));
        when(resultSet.getString("profile_image_id")).thenReturn(null);

        // When
        User actual = userRowMapper.mapRow(resultSet, 1);

        // Then
        assertThat(actual.getId()).isEqualTo(userId);
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getEmail()).isEqualTo(email);
        assertThat(actual.getPassword()).isEqualTo(password);
        assertThat(actual.getProfileImageId()).isNull();
    }
}
