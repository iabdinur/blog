package com.iabdinur.service;

import com.github.javafaker.Faker;
import com.iabdinur.dao.AccountDao;
import com.iabdinur.dto.AccountDTO;
import com.iabdinur.dto.LoginRequest;
import com.iabdinur.dto.LoginResponse;
import com.iabdinur.dto.SendCodeRequest;
import com.iabdinur.dto.VerifyCodeRequest;
import com.iabdinur.model.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private AccountService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private AccountDao accountDao;

    private final Faker FAKER = new Faker();

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        underTest = new AccountService(accountDao, passwordEncoder);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    private Account createTestAccount() {
        Account account = new Account(FAKER.internet().emailAddress(), FAKER.internet().password());
        account.setId(FAKER.random().nextLong());
        return account;
    }

    @Test
    void itShouldLogin() {
        // Given
        Account account = createTestAccount();
        LoginRequest request = new LoginRequest(account.getUsername(), account.getPassword());
        when(accountDao.selectAccountByUsername(account.getUsername())).thenReturn(Optional.of(account));

        // When
        Optional<LoginResponse> result = underTest.login(request);

        // Then
        verify(accountDao).selectAccountByUsername(account.getUsername());
        assertThat(result).isPresent();
        assertThat(result.get().token()).isNotNull();
        assertEquals(account.getUsername(), result.get().account().username());
    }

    @Test
    void itShouldReturnEmptyWhenUsernameNotFound() {
        // Given
        String username = FAKER.internet().emailAddress();
        LoginRequest request = new LoginRequest(username, FAKER.internet().password());
        when(accountDao.selectAccountByUsername(username)).thenReturn(Optional.empty());

        // When
        Optional<LoginResponse> result = underTest.login(request);

        // Then
        verify(accountDao).selectAccountByUsername(username);
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldReturnEmptyWhenPasswordIncorrect() {
        // Given
        Account account = createTestAccount();
        String wrongPassword = FAKER.internet().password();
        LoginRequest request = new LoginRequest(account.getUsername(), wrongPassword);
        when(accountDao.selectAccountByUsername(account.getUsername())).thenReturn(Optional.of(account));

        // When
        Optional<LoginResponse> result = underTest.login(request);

        // Then
        verify(accountDao).selectAccountByUsername(account.getUsername());
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldFindByUsername() {
        // Given
        Account account = createTestAccount();
        when(accountDao.selectAccountByUsername(account.getUsername())).thenReturn(Optional.of(account));

        // When
        Optional<AccountDTO> result = underTest.findByUsername(account.getUsername());

        // Then
        verify(accountDao).selectAccountByUsername(account.getUsername());
        assertThat(result).isPresent();
        assertEquals(account.getUsername(), result.get().username());
    }

    @Test
    void itShouldReturnEmptyWhenUsernameNotFoundInFindByUsername() {
        // Given
        String username = FAKER.internet().emailAddress();
        when(accountDao.selectAccountByUsername(username)).thenReturn(Optional.empty());

        // When
        Optional<AccountDTO> result = underTest.findByUsername(username);

        // Then
        verify(accountDao).selectAccountByUsername(username);
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldCreateAccount() {
        // Given
        String username = FAKER.internet().emailAddress();
        String password = FAKER.internet().password();

        // Mock insertAccount to set an ID on the account
        doAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(FAKER.random().nextLong());
            return null;
        }).when(accountDao).insertAccount(any(Account.class));

        // When
        AccountDTO result = underTest.createAccount(username, password);

        // Then
        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountDao).insertAccount(accountArgumentCaptor.capture());
        verify(passwordEncoder).encode(password);
        Account capturedAccount = accountArgumentCaptor.getValue();
        
        assertEquals(username, capturedAccount.getUsername());
        // Password should be hashed (mocked to return same value for test)
        assertThat(capturedAccount.getPassword()).isNotNull();
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
    }

    @Test
    void itShouldSendVerificationCode() {
        // Given
        Account account = createTestAccount();
        SendCodeRequest request = new SendCodeRequest(account.getUsername());
        when(accountDao.selectAccountByUsername(account.getUsername())).thenReturn(Optional.of(account));

        // When
        underTest.sendVerificationCode(request);

        // Then
        verify(accountDao).selectAccountByUsername(account.getUsername());
        // Code is stored in memory, we can't easily verify it without reflection
    }

    @Test
    void itShouldSendVerificationCodeWhenAccountNotFound() {
        // Given
        String email = FAKER.internet().emailAddress();
        SendCodeRequest request = new SendCodeRequest(email);
        when(accountDao.selectAccountByUsername(email)).thenReturn(Optional.empty());

        // When
        underTest.sendVerificationCode(request);

        // Then
        verify(accountDao).selectAccountByUsername(email);
        // Should not throw exception (security: don't reveal if account exists)
    }

    @Test
    void itShouldVerifyCode() {
        // Given
        Account account = createTestAccount();
        SendCodeRequest sendRequest = new SendCodeRequest(account.getUsername());
        underTest.sendVerificationCode(sendRequest);
        
        // Note: We can't easily get the generated code without reflection
        // So we'll test with a wrong code to verify the flow
        VerifyCodeRequest verifyRequest = new VerifyCodeRequest(account.getUsername(), "000000");
        when(accountDao.selectAccountByUsername(account.getUsername())).thenReturn(Optional.of(account));

        // When
        Optional<LoginResponse> result = underTest.verifyCode(verifyRequest);

        // Then
        // With wrong code, should return empty
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldReturnEmptyWhenCodeNotFound() {
        // Given
        String email = FAKER.internet().emailAddress();
        VerifyCodeRequest request = new VerifyCodeRequest(email, "123456");

        // When
        Optional<LoginResponse> result = underTest.verifyCode(request);

        // Then
        assertThat(result).isEmpty();
    }
}
