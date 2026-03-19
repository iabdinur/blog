package com.iabdinur.controller;

import com.iabdinur.dao.UserDao;
import com.iabdinur.dao.VerificationCodeDao;
import com.iabdinur.dao.AuthorDao;
import com.iabdinur.model.User;
import com.iabdinur.service.AuthorService;
import com.iabdinur.service.EmailService;
import com.iabdinur.service.S3Service;
import com.iabdinur.service.UserService;
import com.iabdinur.util.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(UserControllerProfileImageTest.Config.class)
class UserControllerProfileImageTest {

    @TestConfiguration
    static class Config {
        static class FakeS3Service extends S3Service {
            private software.amazon.awssdk.core.ResponseBytes<software.amazon.awssdk.services.s3.model.GetObjectResponse> nextResponse;

            FakeS3Service() {
                super(null, null);
            }

            void setNextResponse(software.amazon.awssdk.core.ResponseBytes<software.amazon.awssdk.services.s3.model.GetObjectResponse> nextResponse) {
                this.nextResponse = nextResponse;
            }

            @Override
            public software.amazon.awssdk.core.ResponseBytes<software.amazon.awssdk.services.s3.model.GetObjectResponse> getObjectBytes(String key) {
                if (nextResponse == null) {
                    throw new IllegalStateException("FakeS3Service nextResponse not set");
                }
                return nextResponse;
            }
        }

        @Bean
        DataSource dataSource() {
            // Not used by these tests; required to construct JdbcTemplate for AuthorService
            return new org.springframework.jdbc.datasource.AbstractDataSource() {
                @Override
                public java.sql.Connection getConnection() {
                    throw new UnsupportedOperationException("Not used in this test");
                }

                @Override
                public java.sql.Connection getConnection(String username, String password) {
                    throw new UnsupportedOperationException("Not used in this test");
                }
            };
        }

        @Bean
        JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        AuthorService authorService(AuthorDao authorDao, JdbcTemplate jdbcTemplate) {
            return new AuthorService(authorDao, jdbcTemplate);
        }

        @Bean
        JWTUtil jwtUtil() {
            JWTUtil util = new JWTUtil();
            // Ensure a valid HMAC key length for jjwt when invoked in other controller endpoints
            ReflectionTestUtils.setField(util, "secretKey", "test-secret-key-must-be-at-least-32-characters-long");
            ReflectionTestUtils.setField(util, "expiration", 86400000L);
            return util;
        }

        @Bean
        FakeS3Service s3Service() {
            return new FakeS3Service();
        }

        @Bean
        UserService userService(
                UserDao userDao,
                VerificationCodeDao verificationCodeDao,
                PasswordEncoder passwordEncoder,
                EmailService emailService,
                AuthorService authorService,
                JWTUtil jwtUtil,
                S3Service s3Service
        ) {
            return new UserService(userDao, verificationCodeDao, passwordEncoder, emailService, authorService, jwtUtil, s3Service);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDao userDao;

    @MockBean
    private VerificationCodeDao verificationCodeDao;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailService emailService;

    @MockBean
    private AuthorDao authorDao;

    @Autowired
    private Config.FakeS3Service s3Service;

    @Test
    void itShouldReturnProfileImageWithHeadersAndContentType() throws Exception {
        // Given
        String email = "test@example.com";
        String key = "profile-images/test-key.png";
        byte[] bytes = new byte[]{5, 6, 7};

        User user = new User("Test User", email, "hashed:pw");
        user.setId(1L);
        user.setProfileImageId(key);

        when(userDao.selectUserByEmail(email)).thenReturn(java.util.Optional.of(user));

        var responseBytes = software.amazon.awssdk.core.ResponseBytes.fromByteArray(
                software.amazon.awssdk.services.s3.model.GetObjectResponse.builder()
                        .contentType("image/png")
                        .build(),
                bytes
        );
        s3Service.setNextResponse(responseBytes);

        // When/Then
        mockMvc.perform(get("/api/v1/users/{email}/profile-image", email))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")))
                .andExpect(header().string("Pragma", "no-cache"))
                .andExpect(header().string("Expires", "0"))
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(bytes));
    }

    @Test
    void itShouldReturn404WhenUserHasNoProfileImage() throws Exception {
        // Given
        String email = "missing@example.com";
        User user = new User("Test User", email, "hashed:pw");
        user.setId(2L);
        user.setProfileImageId(null);

        when(userDao.selectUserByEmail(email)).thenReturn(java.util.Optional.of(user));

        // When/Then
        mockMvc.perform(get("/api/v1/users/{email}/profile-image", email))
                .andExpect(status().isNotFound());
    }

    @Test
    void itShouldReturn404WhenUserNotFound() throws Exception {
        // Given
        String email = "nouser@example.com";
        when(userDao.selectUserByEmail(email)).thenReturn(java.util.Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/v1/users/{email}/profile-image", email))
                .andExpect(status().isNotFound());
    }
}

