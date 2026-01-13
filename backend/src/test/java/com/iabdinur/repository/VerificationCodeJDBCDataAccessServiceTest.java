package com.iabdinur.repository;

import com.iabdinur.AbstractTestcontainers;
import com.iabdinur.model.VerificationCode;
import com.iabdinur.rowmapper.VerificationCodeRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class VerificationCodeJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private VerificationCodeJDBCDataAccessService underTest;
    private final VerificationCodeRowMapper rowMapper = new VerificationCodeRowMapper();

    @BeforeEach
    void setUp() {
        // Clean up after each test - only if table exists
        try {
            getJdbcTemplate().execute("DELETE FROM verification_codes");
        } catch (Exception e) {
            // Table doesn't exist - this means migrations didn't create it
            // This is a migration issue, but we'll skip cleanup for now
            // The test will fail when trying to use the table, which will indicate the real problem
        }
        underTest = new VerificationCodeJDBCDataAccessService(
                getJdbcTemplate(),
                rowMapper
        );
    }

    private VerificationCode createTestVerificationCode() {
        String email = FAKER.internet().emailAddress();
        String hashedCode = "$2a$10$JWw/S/0M1sZ4TbXioV/lv.JA6lO.aaaBP0qFl3asEseEgMITP0DK6";
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        VerificationCode code = new VerificationCode(email, hashedCode, expiresAt);
        underTest.insertVerificationCode(code);

        return underTest.findActiveCodeByEmail(email)
                .orElseThrow();
    }

    @Test
    void itShouldInsertVerificationCode() {
        // Given
        String email = FAKER.internet().emailAddress();
        String hashedCode = "$2a$10$JWw/S/0M1sZ4TbXioV/lv.JA6lO.aaaBP0qFl3asEseEgMITP0DK6";
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);
        VerificationCode code = new VerificationCode(email, hashedCode, expiresAt);

        // When
        underTest.insertVerificationCode(code);

        // Then
        Optional<VerificationCode> actual = underTest.findActiveCodeByEmail(email);
        assertThat(actual).isPresent();
        assertThat(actual.get().getEmail()).isEqualTo(email);
        assertThat(actual.get().getHashedCode()).isEqualTo(hashedCode);
        assertThat(actual.get().getAttempts()).isEqualTo(0);
        assertThat(actual.get().getIsUsed()).isFalse();
    }

    @Test
    void itShouldFindActiveCodeByEmail() {
        // Given
        VerificationCode code = createTestVerificationCode();

        // When
        Optional<VerificationCode> actual = underTest.findActiveCodeByEmail(code.getEmail());

        // Then
        assertThat(actual).isPresent();
        assertThat(actual.get().getEmail()).isEqualTo(code.getEmail());
        assertThat(actual.get().getHashedCode()).isEqualTo(code.getHashedCode());
    }

    @Test
    void itShouldNotFindExpiredCode() {
        // Given
        String email = FAKER.internet().emailAddress();
        String hashedCode = "$2a$10$JWw/S/0M1sZ4TbXioV/lv.JA6lO.aaaBP0qFl3asEseEgMITP0DK6";
        LocalDateTime expiresAt = LocalDateTime.now().minusMinutes(1); // Already expired
        VerificationCode code = new VerificationCode(email, hashedCode, expiresAt);
        underTest.insertVerificationCode(code);

        // When
        Optional<VerificationCode> actual = underTest.findActiveCodeByEmail(email);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void itShouldNotFindUsedCode() {
        // Given
        VerificationCode code = createTestVerificationCode();
        code.setIsUsed(true);
        underTest.updateVerificationCode(code);

        // When
        Optional<VerificationCode> actual = underTest.findActiveCodeByEmail(code.getEmail());

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void itShouldUpdateVerificationCode() {
        // Given
        VerificationCode code = createTestVerificationCode();
        code.incrementAttempts();
        code.setIsUsed(true);

        // When
        underTest.updateVerificationCode(code);

        // Then
        Optional<VerificationCode> updated = underTest.findActiveCodeByEmail(code.getEmail());
        assertThat(updated).isEmpty(); // Should not find it because it's used

        // Verify by querying directly
        String sql = "SELECT * FROM verification_codes WHERE id = ?";
        VerificationCodeRowMapper mapper = new VerificationCodeRowMapper();
        VerificationCode actual = getJdbcTemplate().query(sql, mapper, code.getId())
                .stream()
                .findFirst()
                .orElseThrow();
        assertThat(actual.getAttempts()).isEqualTo(1);
        assertThat(actual.getIsUsed()).isTrue();
    }

    @Test
    void itShouldDeleteExpiredCodes() {
        // Given
        String email1 = FAKER.internet().emailAddress();
        String email2 = FAKER.internet().emailAddress();
        String hashedCode = "$2a$10$JWw/S/0M1sZ4TbXioV/lv.JA6lO.aaaBP0qFl3asEseEgMITP0DK6";
        
        VerificationCode expiredCode = new VerificationCode(email1, hashedCode, LocalDateTime.now().minusMinutes(1));
        VerificationCode activeCode = new VerificationCode(email2, hashedCode, LocalDateTime.now().plusMinutes(10));
        
        underTest.insertVerificationCode(expiredCode);
        underTest.insertVerificationCode(activeCode);

        // When
        underTest.deleteExpiredCodes();

        // Then
        Optional<VerificationCode> expired = underTest.findActiveCodeByEmail(email1);
        Optional<VerificationCode> active = underTest.findActiveCodeByEmail(email2);
        
        assertThat(expired).isEmpty();
        assertThat(active).isPresent();
    }

    @Test
    void itShouldInvalidateCode() {
        // Given
        VerificationCode code = createTestVerificationCode();

        // When
        underTest.invalidateCode(code.getEmail());

        // Then
        Optional<VerificationCode> actual = underTest.findActiveCodeByEmail(code.getEmail());
        assertThat(actual).isEmpty();
    }

    @Test
    void itShouldCountRecentCodesByEmail() {
        // Given
        String email = FAKER.internet().emailAddress();
        String hashedCode = "$2a$10$JWw/S/0M1sZ4TbXioV/lv.JA6lO.aaaBP0qFl3asEseEgMITP0DK6";
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);
        
        VerificationCode code1 = new VerificationCode(email, hashedCode, expiresAt);
        VerificationCode code2 = new VerificationCode(email, hashedCode, expiresAt);
        VerificationCode code3 = new VerificationCode(email, hashedCode, expiresAt);
        
        underTest.insertVerificationCode(code1);
        underTest.insertVerificationCode(code2);
        underTest.insertVerificationCode(code3);

        // When
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        int count = underTest.countRecentCodesByEmail(email, oneHourAgo);

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    void itShouldNotCountOldCodes() {
        // Given
        String email = FAKER.internet().emailAddress();
        String hashedCode = "$2a$10$JWw/S/0M1sZ4TbXioV/lv.JA6lO.aaaBP0qFl3asEseEgMITP0DK6";
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);
        
        VerificationCode code = new VerificationCode(email, hashedCode, expiresAt);
        code.setCreatedAt(LocalDateTime.now().minusHours(2)); // 2 hours ago
        underTest.insertVerificationCode(code);

        // When
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        int count = underTest.countRecentCodesByEmail(email, oneHourAgo);

        // Then
        assertThat(count).isEqualTo(0);
    }
}
