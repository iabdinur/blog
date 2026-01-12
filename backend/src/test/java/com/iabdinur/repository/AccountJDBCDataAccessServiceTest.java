package com.iabdinur.repository;

import com.iabdinur.AbstractTestcontainers;
import com.iabdinur.model.Account;
import com.iabdinur.rowmapper.AccountRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class AccountJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private AccountJDBCDataAccessService underTest;
    private final AccountRowMapper accountRowMapper = new AccountRowMapper();

    @BeforeEach
    void setUp() {
        // Clean up after each test
        getJdbcTemplate().execute("DELETE FROM accounts");
        underTest = new AccountJDBCDataAccessService(
                getJdbcTemplate(),
                accountRowMapper
        );
    }

    private Account createTestAccount() {
        String username = FAKER.name().username();
        String password = FAKER.internet().password();
        LocalDateTime createdAt = FAKER.date().past(365, TimeUnit.DAYS)
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime updatedAt = FAKER.date().past(30, TimeUnit.DAYS)
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        Account account = new Account(username, password);
        account.setCreatedAt(createdAt);
        account.setUpdatedAt(updatedAt);
        underTest.insertAccount(account);

        return underTest.selectAllAccounts().stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void itShouldSelectAllAccounts() {
        // Given
        Account account = createTestAccount();

        // When
        List<Account> actual = underTest.selectAllAccounts();

        // Then
        assertThat(actual).isNotEmpty();
        assertThat(actual).anyMatch(a -> a.getUsername().equals(account.getUsername()));
    }

    @Test
    void itShouldSelectAccountById() {
        // Given
        Account account = createTestAccount();

        // When
        Optional<Account> actual = underTest.selectAccountById(account.getId());

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(a -> {
            assertThat(a.getId()).isEqualTo(account.getId());
            assertThat(a.getUsername()).isEqualTo(account.getUsername());
            assertThat(a.getPassword()).isEqualTo(account.getPassword());
        });
    }

    @Test
    void itShouldSelectAccountByUsername() {
        // Given
        Account account = createTestAccount();

        // When
        Optional<Account> actual = underTest.selectAccountByUsername(account.getUsername());

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(a -> {
            assertThat(a.getUsername()).isEqualTo(account.getUsername());
        });
    }

    @Test
    void itShouldReturnEmptyWhenSelectAccountById() {
        // Given
        Long invalidAccountId = -1L;

        // When
        Optional<Account> actual = underTest.selectAccountById(invalidAccountId);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void itShouldInsertAccount() {
        // Given
        String username = FAKER.name().username();
        String password = FAKER.internet().password();
        Account account = new Account(username, password);

        // When
        underTest.insertAccount(account);

        // Then
        Optional<Account> actual = underTest.selectAccountByUsername(username);
        assertThat(actual).isPresent().hasValueSatisfying(a -> {
            assertThat(a.getUsername()).isEqualTo(username);
            assertThat(a.getPassword()).isEqualTo(password);
        });
    }

    @Test
    void itShouldExistsAccountWithUsername() {
        // Given
        Account account = createTestAccount();

        // When
        boolean exists = underTest.existsAccountWithUsername(account.getUsername());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldExistsAccountWithUsernameReturnFalseWhenDoesNotExist() {
        // Given
        String username = FAKER.name().username();

        // When
        boolean exists = underTest.existsAccountWithUsername(username);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldExistsAccountById() {
        // Given
        Account account = createTestAccount();

        // When
        boolean exists = underTest.existsAccountById(account.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void itShouldExistsAccountByIdWillReturnFalseWhenAccountIdNotPresent() {
        // Given
        Long accountId = -1L;

        // When
        boolean exists = underTest.existsAccountById(accountId);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void itShouldDeleteAccountById() {
        // Given
        Account account = createTestAccount();

        // When
        underTest.deleteAccountById(account.getId());

        // Then
        Optional<Account> deletedAccount = underTest.selectAccountById(account.getId());
        assertThat(deletedAccount).isEmpty();
    }

    @Test
    void itShouldUpdateAccountUsername() {
        // Given
        Account account = createTestAccount();
        String newUsername = FAKER.name().username();

        Account update = new Account();
        update.setId(account.getId());
        update.setUsername(newUsername);

        // When
        underTest.updateAccount(update);

        // Then
        Optional<Account> actual = underTest.selectAccountById(account.getId());
        assertThat(actual).isPresent().hasValueSatisfying(a -> {
            assertThat(a.getId()).isEqualTo(account.getId());
            assertThat(a.getUsername()).isEqualTo(newUsername);
        });
    }

    @Test
    void itShouldUpdateAccountPassword() {
        // Given
        Account account = createTestAccount();
        String newPassword = FAKER.internet().password();

        Account update = new Account();
        update.setId(account.getId());
        update.setPassword(newPassword);

        // When
        underTest.updateAccount(update);

        // Then
        Optional<Account> actual = underTest.selectAccountById(account.getId());
        assertThat(actual).isPresent().hasValueSatisfying(a -> {
            assertThat(a.getId()).isEqualTo(account.getId());
            assertThat(a.getPassword()).isEqualTo(newPassword);
        });
    }

    @Test
    void itShouldNotUpdateWhenNothingToUpdate() {
        // Given
        Account account = createTestAccount();

        Account update = new Account();
        update.setId(account.getId());

        // When
        underTest.updateAccount(update);

        // Then
        Optional<Account> actual = underTest.selectAccountById(account.getId());
        assertThat(actual).isPresent().hasValueSatisfying(a -> {
            assertThat(a.getId()).isEqualTo(account.getId());
            assertThat(a.getUsername()).isEqualTo(account.getUsername());
            assertThat(a.getPassword()).isEqualTo(account.getPassword());
        });
    }
}
