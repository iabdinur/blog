package com.iabdinur.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
    name = "verification_codes",
    indexes = {
        @Index(name = "idx_verification_codes_email", columnList = "email"),
        @Index(name = "idx_verification_codes_expires_at", columnList = "expires_at")
    }
)
public class VerificationCode {
    
    @Id
    @SequenceGenerator(
        name = "verification_codes_id_seq",
        sequenceName = "verification_codes_id_seq",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "verification_codes_id_seq"
    )
    private Long id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String email;
    
    @Column(name = "hashed_code", nullable = false, columnDefinition = "TEXT")
    private String hashedCode;
    
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private Integer attempts = 0;
    
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;
    
    public VerificationCode() {
    }
    
    public VerificationCode(String email, String hashedCode, LocalDateTime expiresAt) {
        this.email = email;
        this.hashedCode = hashedCode;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.attempts = 0;
        this.isUsed = false;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getHashedCode() {
        return hashedCode;
    }
    
    public void setHashedCode(String hashedCode) {
        this.hashedCode = hashedCode;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public Integer getAttempts() {
        return attempts;
    }
    
    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }
    
    public void incrementAttempts() {
        this.attempts++;
    }
    
    public Boolean getIsUsed() {
        return isUsed;
    }
    
    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerificationCode that = (VerificationCode) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "VerificationCode{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", attempts=" + attempts +
                ", isUsed=" + isUsed +
                '}';
    }
}
