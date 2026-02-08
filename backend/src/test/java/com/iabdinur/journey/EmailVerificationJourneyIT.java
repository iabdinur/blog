package com.iabdinur.journey;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.iabdinur.AbstractTestcontainers;
import com.iabdinur.dto.LoginResponse;
import com.iabdinur.dto.SendCodeRequest;
import com.iabdinur.dto.VerifyCodeRequest;
import com.iabdinur.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.iabdinur.BlogApp.class)
@AutoConfigureMockMvc
@Import(com.iabdinur.TestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmailVerificationJourneyIT extends AbstractTestcontainers {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String USER_PATH = "/api/v1/users";
    private final Faker faker = new Faker();

    @BeforeEach
    void setUp() {
        // Clean up before each test
        jdbcTemplate.execute("DELETE FROM sent_emails");
        jdbcTemplate.execute("DELETE FROM verification_codes");
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    void shouldCompleteEmailVerificationJourney() throws Exception {
        // Given
        String email = faker.internet().emailAddress();
        SendCodeRequest sendCodeRequest = new SendCodeRequest(email);

        // Step 1: Send verification code
        mockMvc.perform(post(USER_PATH + "/send-code")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendCodeRequest)))
                .andExpect(status().isOk());

        // Step 2: Verify verification_codes table has entry
        Integer codeCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM verification_codes WHERE email = ?",
            Integer.class,
            email
        );
        assertThat(codeCount).isEqualTo(1);

        // Step 3: Get the verification code from database
        Map<String, Object> codeData = jdbcTemplate.queryForMap(
            "SELECT code, expires_at FROM verification_codes WHERE email = ? ORDER BY created_at DESC LIMIT 1",
            email
        );
        String verificationCode = (String) codeData.get("code");
        assertThat(verificationCode).isNotNull();
        assertThat(verificationCode).hasSize(6);

        // Step 4: Verify sent_emails table has entry
        Integer emailCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM sent_emails WHERE recipient_email = ?",
            Integer.class,
            email
        );
        assertThat(emailCount).isGreaterThanOrEqualTo(1);

        // Step 5: Verify the code
        VerifyCodeRequest verifyCodeRequest = new VerifyCodeRequest(email, verificationCode);
        
        MvcResult result = mockMvc.perform(post(USER_PATH + "/verify-code")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyCodeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value(email))
                .andExpect(jsonPath("$.user.id").exists())
                .andReturn();

        // Step 6: Extract JWT token
        String jwtToken = result.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        assertThat(jwtToken).isNotNull();

        // Step 7: Parse response
        String responseBody = result.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);
        
        assertThat(loginResponse.user().email()).isEqualTo(email);
        assertThat(loginResponse.token()).isNotNull();

        // Step 8: Verify user was created in database
        Integer userCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE email = ?",
            Integer.class,
            email
        );
        assertThat(userCount).isEqualTo(1);

        // Step 9: Verify JWT token is valid
        assertThat(jwtUtil.isTokenValid(jwtToken, email)).isTrue();

        // Step 10: Verify code is marked as used
        Boolean isUsed = jdbcTemplate.queryForObject(
            "SELECT is_used FROM verification_codes WHERE email = ? AND code = ?",
            Boolean.class,
            email,
            verificationCode
        );
        assertThat(isUsed).isTrue();
    }

    @Test
    void shouldRejectInvalidVerificationCode() throws Exception {
        // Given
        String email = faker.internet().emailAddress();
        SendCodeRequest sendCodeRequest = new SendCodeRequest(email);

        // Step 1: Send verification code
        mockMvc.perform(post(USER_PATH + "/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendCodeRequest)))
                .andExpect(status().isOk());

        // Step 2: Try to verify with WRONG code
        VerifyCodeRequest wrongCodeRequest = new VerifyCodeRequest(email, "999999");
        
        mockMvc.perform(post(USER_PATH + "/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongCodeRequest)))
                .andExpect(status().isUnauthorized());

        // Step 3: Verify user was NOT created
        Integer userCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE email = ?",
            Integer.class,
            email
        );
        assertThat(userCount).isEqualTo(0);
    }

    @Test
    void shouldRejectExpiredVerificationCode() throws Exception {
        // Given
        String email = faker.internet().emailAddress();
        SendCodeRequest sendCodeRequest = new SendCodeRequest(email);

        // Step 1: Send verification code
        mockMvc.perform(post(USER_PATH + "/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendCodeRequest)))
                .andExpect(status().isOk());

        // Step 2: Get the code
        String verificationCode = jdbcTemplate.queryForObject(
            "SELECT code FROM verification_codes WHERE email = ?",
            String.class,
            email
        );

        // Step 3: Manually expire the code by setting expires_at to the past
        jdbcTemplate.update(
            "UPDATE verification_codes SET expires_at = ? WHERE email = ?",
            LocalDateTime.now().minusMinutes(1),
            email
        );

        // Step 4: Try to verify expired code
        VerifyCodeRequest verifyCodeRequest = new VerifyCodeRequest(email, verificationCode);
        
        mockMvc.perform(post(USER_PATH + "/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyCodeRequest)))
                .andExpect(status().isUnauthorized());

        // Step 5: Verify user was NOT created
        Integer userCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE email = ?",
            Integer.class,
            email
        );
        assertThat(userCount).isEqualTo(0);
    }

    @Test
    void shouldAllowResendingVerificationCode() throws Exception {
        // Given
        String email = faker.internet().emailAddress();
        SendCodeRequest sendCodeRequest = new SendCodeRequest(email);

        // Step 1: Send first code
        mockMvc.perform(post(USER_PATH + "/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendCodeRequest)))
                .andExpect(status().isOk());

        String firstCode = jdbcTemplate.queryForObject(
            "SELECT code FROM verification_codes WHERE email = ? ORDER BY created_at DESC LIMIT 1",
            String.class,
            email
        );

        // Step 2: Send second code (resend)
        mockMvc.perform(post(USER_PATH + "/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendCodeRequest)))
                .andExpect(status().isOk());

        // Step 3: Verify database has 2 codes
        Integer codeCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM verification_codes WHERE email = ?",
            Integer.class,
            email
        );
        assertThat(codeCount).isEqualTo(2);

        // Step 4: Get latest code
        String latestCode = jdbcTemplate.queryForObject(
            "SELECT code FROM verification_codes WHERE email = ? ORDER BY created_at DESC LIMIT 1",
            String.class,
            email
        );
        assertThat(latestCode).isNotEqualTo(firstCode);

        // Step 5: Verify latest code works
        VerifyCodeRequest verifyCodeRequest = new VerifyCodeRequest(email, latestCode);
        
        mockMvc.perform(post(USER_PATH + "/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyCodeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value(email));

        // Step 6: Verify old code is now invalid (user already created)
        VerifyCodeRequest oldCodeRequest = new VerifyCodeRequest(email, firstCode);
        
        mockMvc.perform(post(USER_PATH + "/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(oldCodeRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldHandleMultipleSimultaneousRegistrations() throws Exception {
        // Given
        String[] emails = new String[5];
        String[] codes = new String[5];
        
        for (int i = 0; i < 5; i++) {
            emails[i] = faker.internet().emailAddress();
        }

        // Step 1: Send codes to all 5 emails
        for (String email : emails) {
            SendCodeRequest sendCodeRequest = new SendCodeRequest(email);
            mockMvc.perform(post(USER_PATH + "/send-code")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sendCodeRequest)))
                    .andExpect(status().isOk());
        }

        // Step 2: Get all verification codes
        for (int i = 0; i < 5; i++) {
            codes[i] = jdbcTemplate.queryForObject(
                "SELECT code FROM verification_codes WHERE email = ?",
                String.class,
                emails[i]
            );
        }

        // Step 3: Verify all 5 codes
        for (int i = 0; i < 5; i++) {
            VerifyCodeRequest verifyCodeRequest = new VerifyCodeRequest(emails[i], codes[i]);
            
            mockMvc.perform(post(USER_PATH + "/verify-code")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(verifyCodeRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.user.email").value(emails[i]))
                    .andExpect(jsonPath("$.token").exists());
        }

        // Step 4: Verify all 5 users exist in database
        Integer userCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users",
            Integer.class
        );
        assertThat(userCount).isEqualTo(5);

        // Step 5: Verify all codes are marked as used
        Integer usedCodeCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM verification_codes WHERE is_used = true",
            Integer.class
        );
        assertThat(usedCodeCount).isEqualTo(5);
    }
}
