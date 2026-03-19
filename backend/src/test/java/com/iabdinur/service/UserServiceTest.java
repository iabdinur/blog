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
import com.iabdinur.util.JWTUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserServiceTest {

    static class FakeS3Service extends S3Service {
        private final AtomicReference<String> generatedKey = new AtomicReference<>("profile-images/test-key.jpg");
        private final AtomicReference<byte[]> objectBytes = new AtomicReference<>(new byte[]{1, 2, 3});
        private final AtomicReference<software.amazon.awssdk.core.ResponseBytes<software.amazon.awssdk.services.s3.model.GetObjectResponse>> objectResponseBytes =
                new AtomicReference<>(software.amazon.awssdk.core.ResponseBytes.fromByteArray(
                        software.amazon.awssdk.services.s3.model.GetObjectResponse.builder()
                                .contentType("image/jpeg")
                                .build(),
                        new byte[]{1, 2, 3}
                ));

        FakeS3Service() {
            super(null, null);
        }

        void setGeneratedKey(String key) {
            generatedKey.set(key);
        }

        void setObjectBytes(byte[] bytes, String contentType) {
            objectBytes.set(bytes);
            objectResponseBytes.set(software.amazon.awssdk.core.ResponseBytes.fromByteArray(
                    software.amazon.awssdk.services.s3.model.GetObjectResponse.builder()
                            .contentType(contentType)
                            .build(),
                    bytes
            ));
        }

        @Override
        public String generateKey(String prefix, String filename) {
            return generatedKey.get();
        }

        @Override
        public void putObject(String key, byte[] file, String contentType) {
            setObjectBytes(file, contentType);
        }

        @Override
        public byte[] getObject(String key) {
            return objectBytes.get();
        }

        @Override
        public software.amazon.awssdk.core.ResponseBytes<software.amazon.awssdk.services.s3.model.GetObjectResponse> getObjectBytes(String key) {
            return objectResponseBytes.get();
        }

        @Override
        public void deleteObject(String key) {
            // no-op for tests
        }
    }

    private UserService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private UserDao userDao;

    @Mock
    private VerificationCodeDao verificationCodeDao;

    @Mock
    private EmailService emailService;

    private FakeS3Service s3Service;

    private AuthorService authorService;
    private JWTUtil jwtUtil;

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
        // Mock email service to do nothing (no-op)
        doNothing().when(emailService).sendVerificationCode(anyString(), anyString(), anyInt());
        // Use fake S3 service (avoids Mockito inline-mock limitations on newer JDKs)
        s3Service = new FakeS3Service();

        // Fake AuthorService - avoids Mockito class-mocking on newer JDKs
        var dataSource = new org.springframework.jdbc.datasource.AbstractDataSource() {
            @Override
            public java.sql.Connection getConnection() {
                throw new UnsupportedOperationException("Not used in this test");
            }

            @Override
            public java.sql.Connection getConnection(String username, String password) {
                throw new UnsupportedOperationException("Not used in this test");
            }
        };
        var jdbcTemplate = new org.springframework.jdbc.core.JdbcTemplate(dataSource);
        var authorDao = new com.iabdinur.dao.AuthorDao() {
            @Override public java.util.List<com.iabdinur.model.Author> selectAllAuthors() { return java.util.List.of(); }
            @Override public java.util.Optional<com.iabdinur.model.Author> selectAuthorById(Long authorId) { return java.util.Optional.empty(); }
            @Override public java.util.Optional<com.iabdinur.model.Author> selectAuthorByUsername(String username) { return java.util.Optional.empty(); }
            @Override public void insertAuthor(com.iabdinur.model.Author author) { }
            @Override public boolean existsAuthorWithUsername(String username) { return false; }
            @Override public boolean existsAuthorWithEmail(String email) { return false; }
            @Override public boolean existsAuthorById(Long authorId) { return false; }
            @Override public void deleteAuthorById(Long authorId) { }
            @Override public void updateAuthor(com.iabdinur.model.Author update) { }
        };
        this.authorService = new AuthorService(authorDao, jdbcTemplate) {
            @Override
            public java.util.Optional<com.iabdinur.dto.AuthorDTO> getAuthorByEmail(String email) {
                return java.util.Optional.empty();
            }
        };

        // Fake JWTUtil - deterministic token for tests
        this.jwtUtil = new JWTUtil() {
            @Override
            public String issueToken(String username, java.util.List<String> roles) {
                return "jwt-token-for-" + username;
            }
        };

        underTest = new UserService(userDao, verificationCodeDao, passwordEncoder, emailService, authorService, jwtUtil, s3Service);
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

    @Test
    void itShouldReturnProfileImageBytesWithContentTypeAndNoCacheHeaders() {
        // Given
        String email = "test@example.com";
        String key = "profile-images/test-key.png";
        byte[] bytes = new byte[]{10, 20, 30};

        User user = new User("Test User", email, "hashed:pw");
        user.setId(1L);
        user.setProfileImageId(key);

        when(userDao.selectUserByEmail(email)).thenReturn(Optional.of(user));

        var response = software.amazon.awssdk.core.ResponseBytes.fromByteArray(
                software.amazon.awssdk.services.s3.model.GetObjectResponse.builder()
                        .contentType("image/png")
                        .build(),
                bytes
        );
        s3Service.setObjectBytes(bytes, "image/png");

        // When
        var result = underTest.getUserProfileImageBytes(email);

        // Then
        assertNotNull(result);
        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_PNG);
        assertThat(result.getHeaders().getFirst("Cache-Control")).contains("no-store");
        assertThat(result.getHeaders().getFirst("Pragma")).isEqualTo("no-cache");
        assertThat(result.getHeaders().getFirst("Expires")).isEqualTo("0");
        assertThat(result.getBody()).isEqualTo(bytes);

        verify(userDao).selectUserByEmail(email);
        // Fake S3Service is used; method-call verification is not applicable
    }

    @Test
    void itShouldFallbackToOctetStreamWhenContentTypeMissing() {
        // Given
        String email = "test2@example.com";
        String key = "profile-images/test-key";
        byte[] bytes = new byte[]{1, 2};

        User user = new User("Test User", email, "hashed:pw");
        user.setId(2L);
        user.setProfileImageId(key);

        when(userDao.selectUserByEmail(email)).thenReturn(Optional.of(user));

        var response = software.amazon.awssdk.core.ResponseBytes.fromByteArray(
                software.amazon.awssdk.services.s3.model.GetObjectResponse.builder()
                        .contentType(null)
                        .build(),
                bytes
        );
        s3Service.setObjectBytes(bytes, null);

        // When
        var result = underTest.getUserProfileImageBytes(email);

        // Then
        assertThat(result.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_OCTET_STREAM);
        assertThat(result.getBody()).isEqualTo(bytes);
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
        // Fake AuthorService is used; method-call verification is not applicable
        // Fake JWTUtil is used; method-call verification is not applicable
        assertThat(result).isPresent();
        assertThat(result.get().token()).isNotNull();
        assertThat(result.get().token()).startsWith("jwt-token-for-");
        assertEquals(user.getEmail(), result.get().user().email());
        assertThat(result.get().author()).isNull(); // No author by default
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
        // Verify email service was called
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> expiresCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(emailService).sendVerificationCode(emailCaptor.capture(), codeCaptor.capture(), expiresCaptor.capture());
        assertThat(emailCaptor.getValue()).isEqualTo(user.getEmail());
        assertThat(codeCaptor.getValue()).matches("\\d{6}"); // 6-digit code
        assertThat(expiresCaptor.getValue()).isEqualTo(10); // 10 minutes
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
