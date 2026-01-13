package com.iabdinur.journey;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.iabdinur.AbstractTestcontainers;
import com.iabdinur.dto.AuthenticationRequest;
import com.iabdinur.dto.AuthenticationResponse;
import com.iabdinur.dto.UserDTO;
import com.iabdinur.dto.UserRegistrationRequest;
import com.iabdinur.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.iabdinur.BlogApp.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthenticationIT extends AbstractTestcontainers {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String AUTHENTICATION_PATH = "/api/v1/auth";
    private static final String USER_PATH = "/api/v1/users";

    @BeforeEach
    void setUp() {
        // Clean up after each test
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    void canLogin() throws Exception {
        // Given
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.internet().emailAddress();
        String password = "password";

        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                name,
                email,
                password
        );

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                email,
                password
        );

        // Try to login before registration - should fail
        mockMvc.perform(post(AUTHENTICATION_PATH + "/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isUnauthorized());

        // Register user
        mockMvc.perform(post(USER_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.id").exists());

        // Login after registration - should succeed
        MvcResult result = mockMvc.perform(post(AUTHENTICATION_PATH + "/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value(email))
                .andReturn();

        // Extract JWT token from Authorization header
        String jwtToken = result.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        assertThat(jwtToken).isNotNull();

        // Parse response body
        String responseBody = result.getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(
                responseBody,
                AuthenticationResponse.class
        );

        UserDTO userDTO = authenticationResponse.user();

        // Verify JWT token is valid
        assertThat(jwtUtil.isTokenValid(jwtToken, userDTO.email())).isTrue();

        // Verify user details
        assertThat(userDTO.email()).isEqualTo(email);
        assertThat(userDTO.id()).isNotNull();
        assertThat(userDTO.createdAt()).isNotNull();
        assertThat(userDTO.updatedAt()).isNotNull();
    }
}
