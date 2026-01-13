package com.iabdinur.dao;

import com.iabdinur.model.VerificationCode;
import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationCodeDao {
    void insertVerificationCode(VerificationCode verificationCode);
    Optional<VerificationCode> findActiveCodeByEmail(String email);
    void updateVerificationCode(VerificationCode verificationCode);
    void deleteExpiredCodes();
    void invalidateCode(String email);
    int countRecentCodesByEmail(String email, LocalDateTime since);
}
