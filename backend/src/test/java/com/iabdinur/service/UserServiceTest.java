package com.iabdinur.service;

import com.github.javafaker.Faker;
import com.iabdinur.dao.UserDao;
import com.iabdinur.dao.VerificationCodeDao;
import com.iabdinur.dto.LoginRequest;
import com.iabdinur.dto.LoginResponse;
import com.iabdinur.dto.SendCodeRequest;
import com.iabdinur.dto.UserDTO;
import com.iabdinur.dto.VerifyCodeRequest;
import com.iabdinur.model.User;
import com.iabdinur.model.VerificationCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private UserDao userDao;

    @Mock
    private VerificationCodeDao verificationCodeDao;

    private final Faker FAKER = new Faker();

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> {
            String raw = invocation.getArgument(0);
            // Return a "hashed" version (prefixed with "hashed:" for testing)
            return "hashed:" + raw;
        });
        when(passwordEncoder.matches(anyString(), anyString())).thenAnswer(invocation -> {
            String raw = invocation.getArgument(0);
            String encoded = invocation.getArgument(1);
            // For testing, check if encoded password/code starts with "hashed:" and matches raw
            return encoded.equals("hashed:" + raw);
        });
        underTest = new UserService(userDao, verificationCodeDao, passwordEncoder);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    private User createTestUser() {
        String name = FAKER.name().fullName();
        String email = FAKER.internet().emailAddress();
        String plainPassword = FAKER.internet().password();
        // In real scenario, password would be hashed, so we'll use "hashed:" prefix for testing
        String hashedPassword = "hashed:" + plainPassword;
        User user = new User(name, email, hashedPassword);
        user.setId(FAKER.random().nextLong());
        return user;
    }
    
    private User createTestUserWithPlainPassword(String plainPassword) {
        String name = FAKER.name().fullName();
        String email = FAKER.internet().emailAddress();
        String hashedPassword = "hashed:" + plainPassword;
        User user = new User(name, email, hashedPassword);
        user.setId(FAKER.random().nextLong());
        return user;
    }

    @Test
    void itShouldLogin() {
        // Given
        String plainPassword = FAKER.internet().password();
        User user = createTestUserWithPlainPassword(plainPassword);
        LoginRequest request = new LoginRequest(user.getEmail(), plainPassword);
        when(userDao.selectUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // When
        Optional<LoginResponse> result = underTest.login(request);

        // Then
        verify(userDao).selectUserByEmail(user.getEmail());
        verify(passwordEncoder).matches(plainPassword, user.getPassword());
        assertThat(result).isPresent();
        assertThat(result.get().token()).isNotNull();
        assertEquals(user.getEmail(), result.get().user().email());
    }

    @Test
    void itShouldReturnEmptyWhenEmailNotFound() {
        // Given
        String email = FAKER.internet().emailAddress();
        LoginRequest request = new LoginRequest(email, FAKER.internet().password());
        when(userDao.selectUserByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<LoginResponse> result = underTest.login(request);

        // Then
        verify(userDao).selectUserByEmail(email);
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldReturnEmptyWhenPasswordIncorrect() {
        // Given
        String correctPassword = FAKER.internet().password();
        User user = createTestUserWithPlainPassword(correctPassword);
        String wrongPassword = FAKER.internet().password();
        LoginRequest request = new LoginRequest(user.getEmail(), wrongPassword);
        when(userDao.selectUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // When
        Optional<LoginResponse> result = underTest.login(request);

        // Then
        verify(userDao).selectUserByEmail(user.getEmail());
        verify(passwordEncoder).matches(wrongPassword, user.getPassword());
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldFindByEmail() {
        // Given
        User user = createTestUser();
        when(userDao.selectUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // When
        Optional<UserDTO> result = underTest.findByEmail(user.getEmail());

        // Then
        verify(userDao).selectUserByEmail(user.getEmail());
        assertThat(result).isPresent();
        assertEquals(user.getEmail(), result.get().email());
    }

    @Test
    void itShouldReturnEmptyWhenEmailNotFoundInFindByEmail() {
        // Given
        String email = FAKER.internet().emailAddress();
        when(userDao.selectUserByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<UserDTO> result = underTest.findByEmail(email);

        // Then
        verify(userDao).selectUserByEmail(email);
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldCreateUser() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().emailAddress();
        String password = FAKER.internet().password();

        // Mock insertUser to set an ID on the user
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(FAKER.random().nextLong());
            return null;
        }).when(userDao).insertUser(any(User.class));

        // When
        UserDTO result = underTest.createUser(name, email, password);

        // Then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).insertUser(userArgumentCaptor.capture());
        verify(passwordEncoder).encode(password);
        User capturedUser = userArgumentCaptor.getValue();
        
        assertEquals(name, capturedUser.getName());
        assertEquals(email, capturedUser.getEmail());
        // Password should be hashed (mocked to return same value for test)
        assertThat(capturedUser.getPassword()).isNotNull();
        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
    }

    @Test
    void itShouldSendVerificationCode() {
        // Given
        User user = createTestUser();
        SendCodeRequest request = new SendCodeRequest(user.getEmail());
        when(userDao.selectUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(verificationCodeDao.countRecentCodesByEmail(anyString(), any(LocalDateTime.class))).thenReturn(0);

        // When
        underTest.sendVerificationCode(request);

        // Then
        verify(userDao).selectUserByEmail(user.getEmail());
        verify(verificationCodeDao).invalidateCode(user.getEmail());
        verify(verificationCodeDao).insertVerificationCode(any(VerificationCode.class));
        verify(verificationCodeDao).deleteExpiredCodes();
    }

    @Test
    void itShouldSendVerificationCodeWhenUserNotFound() {
        // Given
        String email = FAKER.internet().emailAddress();
        SendCodeRequest request = new SendCodeRequest(email);
        when(userDao.selectUserByEmail(email)).thenReturn(Optional.empty());
        when(verificationCodeDao.countRecentCodesByEmail(anyString(), any(LocalDateTime.class))).thenReturn(0);

        // When
        underTest.sendVerificationCode(request);

        // Then
        verify(userDao).selectUserByEmail(email);
        // Should not throw exception (security: don't reveal if user exists)
        verify(verificationCodeDao, never()).insertVerificationCode(any());
    }

    @Test
    void itShouldNotSendCodeWhenRateLimitExceeded() {
        // Given
        String email = FAKER.internet().emailAddress();
        SendCodeRequest request = new SendCodeRequest(email);
        when(verificationCodeDao.countRecentCodesByEmail(anyString(), any(LocalDateTime.class))).thenReturn(3); // Max is 3

        // When
        underTest.sendVerificationCode(request);

        // Then
        // When rate limit is exceeded, it returns early without checking user
        verify(userDao, never()).selectUserByEmail(anyString());
        verify(verificationCodeDao, never()).insertVerificationCode(any());
    }

    @Test
    void itShouldVerifyCodeSuccessfully() {
        // Given
        String code = "123456";
        String hashedCode = "hashed:" + code;
        User user = createTestUser();
        VerificationCode verificationCode = new VerificationCode(
            user.getEmail(),
            hashedCode,
            LocalDateTime.now().plusMinutes(10)
        );
        verificationCode.setId(1L);
        verificationCode.setAttempts(0);
        
        when(verificationCodeDao.findActiveCodeByEmail(user.getEmail())).thenReturn(Optional.of(verificationCode));
        when(userDao.selectUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // When
        Optional<LoginResponse> result = underTest.verifyCode(new VerifyCodeRequest(user.getEmail(), code));

        // Then
        verify(verificationCodeDao).findActiveCodeByEmail(user.getEmail());
        verify(passwordEncoder).matches(code, hashedCode);
        verify(verificationCodeDao).updateVerificationCode(any(VerificationCode.class));
        verify(userDao).selectUserByEmail(user.getEmail());
        assertThat(result).isPresent();
        assertThat(result.get().token()).isNotNull();
    }

    @Test
    void itShouldReturnEmptyWhenCodeNotFound() {
        // Given
        String email = FAKER.internet().emailAddress();
        VerifyCodeRequest request = new VerifyCodeRequest(email, "123456");
        when(verificationCodeDao.findActiveCodeByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<LoginResponse> result = underTest.verifyCode(request);

        // Then
        verify(verificationCodeDao).findActiveCodeByEmail(email);
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldReturnEmptyWhenCodeIsWrong() {
        // Given
        String correctCode = "123456";
        String wrongCode = "000000";
        String hashedCode = "hashed:" + correctCode;
        User user = createTestUser();
        VerificationCode verificationCode = new VerificationCode(
            user.getEmail(),
            hashedCode,
            LocalDateTime.now().plusMinutes(10)
        );
        verificationCode.setId(1L);
        verificationCode.setAttempts(0);
        
        when(verificationCodeDao.findActiveCodeByEmail(user.getEmail())).thenReturn(Optional.of(verificationCode));

        // When
        Optional<LoginResponse> result = underTest.verifyCode(new VerifyCodeRequest(user.getEmail(), wrongCode));

        // Then
        verify(verificationCodeDao).findActiveCodeByEmail(user.getEmail());
        verify(passwordEncoder).matches(wrongCode, hashedCode);
        verify(verificationCodeDao).updateVerificationCode(any(VerificationCode.class));
        assertThat(result).isEmpty();
    }

    @Test
    void itShouldIncrementAttemptsOnWrongCode() {
        // Given
        String correctCode = "123456";
        String wrongCode = "000000";
        String hashedCode = "hashed:" + correctCode;
        User user = createTestUser();
        VerificationCode verificationCode = new VerificationCode(
            user.getEmail(),
            hashedCode,
            LocalDateTime.now().plusMinutes(10)
        );
        verificationCode.setId(1L);
        verificationCode.setAttempts(0);
        
        when(verificationCodeDao.findActiveCodeByEmail(user.getEmail())).thenReturn(Optional.of(verificationCode));

        // When
        underTest.verifyCode(new VerifyCodeRequest(user.getEmail(), wrongCode));

        // Then
        ArgumentCaptor<VerificationCode> captor = ArgumentCaptor.forClass(VerificationCode.class);
        verify(verificationCodeDao).updateVerificationCode(captor.capture());
        assertThat(captor.getValue().getAttempts()).isEqualTo(1);
    }
}
