package com.iabdinur.repository;

import com.iabdinur.AbstractTestcontainers;
import com.iabdinur.model.User;
import com.iabdinur.model.UserType;
import com.iabdinur.rowmapper.UserRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class UserJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private UserJDBCDataAccessService underTest;
    private final UserRowMapper userRowMapper = new UserRowMapper();

    @BeforeEach
    void setUp() {
        // Clean up after each test
        getJdbcTemplate().execute("DELETE FROM users");
        underTest = new UserJDBCDataAccessService(
                getJdbcTemplate(),
                userRowMapper
        );
    }

    private User createTestUser() {
        String name = FAKER.name().fullName();
        String email = FAKER.internet().emailAddress();
        String password = FAKER.internet().password();
        LocalDateTime createdAt = FAKER.date().past(365, TimeUnit.DAYS)
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updatedAt = FAKER.date().past(30, TimeUnit.DAYS)
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        User user = new User(name, email, password, UserType.REA);
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);
        underTest.insertUser(user);

        return underTest.selectAllUsers().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void itShouldSelectAllUsers() {
        // Given
        User user = createTestUser();

        // When
        List<User> actual = underTest.selectAllUsers();

        // Then
        assertThat(actual).isNotEmpty();
        assertThat(actual).anyMatch(u -> u.getEmail().equals(user.getEmail()));
    }

    @Test
    void itShouldSelectUserById() {
        // Given
        User user = createTestUser();

        // When
        Optional<User> actual = underTest.selectUserById(user.getId());

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(u -> {
            assertThat(u.getId()).isEqualTo(user.getId());
            assertThat(u.getEmail()).isEqualTo(user.getEmail());
            assertThat(u.getPassword()).isEqualTo(user.getPassword());
        });
    }

    @Test
    void itShouldSelectUserByEmail() {
        // Given
        User user = createTestUser();

        // When
        Optional<User> actual = underTest.selectUserByEmail(user.getEmail());

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(u -> {
            assertThat(u.getEmail()).isEqualTo(user.getEmail());
        });
    }

    @Test
    void itShouldReturnEmptyWhenSelectUserById() {
        // Given
        Long invalidUserId = -1L;

        // When
        Optional<User> actual = underTest.selectUserById(invalidUserId);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void itShouldInsertUser() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().emailAddress();
        String password = FAKER.internet().password();
        User user = new User(name, email, password, UserType.REA);

        // When
        underTest.insertUser(user);

        // Then
        Optional<User> actual = underTest.selectUserByEmail(email);
        assertThat(actual).isPresent().hasValueSatisfying(u -> {
            assertThat(u.getEmail()).isEqualTo(email);
            assertThat(u.getName()).isEqualTo(name);
            assertThat(u.getPassword()).isEqualTo(password);
        });
    }

    @Test
    void itShouldExistsUserWithEmail() {
        // Given
        User user = createTestUser();

        // When
        boolean exists = underTest.existsUserWithEmail(user.getEmail());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldExistsUserWithEmailReturnFalseWhenDoesNotExist() {
        // Given
        String email = FAKER.internet().emailAddress();

        // When
        boolean exists = underTest.existsUserWithEmail(email);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldExistsUserById() {
        // Given
        User user = createTestUser();

        // When
        boolean exists = underTest.existsUserById(user.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldExistsUserByIdWillReturnFalseWhenUserIdNotPresent() {
        // Given
        Long userId = -1L;

        // When
        boolean exists = underTest.existsUserById(userId);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldDeleteUserById() {
        // Given
        User user = createTestUser();

        // When
        underTest.deleteUserById(user.getId());

        // Then
        Optional<User> deletedUser = underTest.selectUserById(user.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void itShouldUpdateUserName() {
        // Given
        User user = createTestUser();
        String newName = FAKER.name().fullName();

        User update = new User();
        update.setId(user.getId());
        update.setName(newName);

        // When
        underTest.updateUser(update);

        // Then
        Optional<User> actual = underTest.selectUserById(user.getId());
        assertThat(actual).isPresent().hasValueSatisfying(u -> {
            assertThat(u.getId()).isEqualTo(user.getId());
            assertThat(u.getName()).isEqualTo(newName);
        });
    }

    @Test
    void itShouldUpdateUserEmail() {
        // Given
        User user = createTestUser();
        String newEmail = FAKER.internet().emailAddress();

        User update = new User();
        update.setId(user.getId());
        update.setEmail(newEmail);

        // When
        underTest.updateUser(update);

        // Then
        Optional<User> actual = underTest.selectUserById(user.getId());
        assertThat(actual).isPresent().hasValueSatisfying(u -> {
            assertThat(u.getId()).isEqualTo(user.getId());
            assertThat(u.getEmail()).isEqualTo(newEmail);
        });
    }

    @Test
    void itShouldUpdateUserPassword() {
        // Given
        User user = createTestUser();
        String newPassword = FAKER.internet().password();

        User update = new User();
        update.setId(user.getId());
        update.setPassword(newPassword);

        // When
        underTest.updateUser(update);

        // Then
        Optional<User> actual = underTest.selectUserById(user.getId());
        assertThat(actual).isPresent().hasValueSatisfying(u -> {
            assertThat(u.getId()).isEqualTo(user.getId());
            assertThat(u.getPassword()).isEqualTo(newPassword);
        });
    }

    @Test
    void itShouldUpdateUserProfileImageId() {
        // Given
        User user = createTestUser();
        String newProfileImageId = FAKER.internet().uuid();

        User update = new User();
        update.setId(user.getId());
        update.setProfileImageId(newProfileImageId);

        // When
        underTest.updateUser(update);

        // Then
        Optional<User> actual = underTest.selectUserById(user.getId());
        assertThat(actual).isPresent().hasValueSatisfying(u -> {
            assertThat(u.getId()).isEqualTo(user.getId());
            assertThat(u.getProfileImageId()).isEqualTo(newProfileImageId);
        });
    }

    @Test
    void itShouldNotUpdateWhenNothingToUpdate() {
        // Given
        User user = createTestUser();

        User update = new User();
        update.setId(user.getId());

        // When
        underTest.updateUser(update);

        // Then
        Optional<User> actual = underTest.selectUserById(user.getId());
        assertThat(actual).isPresent().hasValueSatisfying(u -> {
            assertThat(u.getId()).isEqualTo(user.getId());
            assertThat(u.getEmail()).isEqualTo(user.getEmail());
            assertThat(u.getPassword()).isEqualTo(user.getPassword());
        });
    }
}
