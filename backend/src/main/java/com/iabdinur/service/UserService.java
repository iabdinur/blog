package com.iabdinur.service;

import com.iabdinur.dao.UserDao;
import com.iabdinur.dao.VerificationCodeDao;
import com.iabdinur.dto.LoginRequest;
import com.iabdinur.dto.LoginResponse;
import com.iabdinur.dto.SendCodeRequest;
import com.iabdinur.dto.UserDTO;
import com.iabdinur.dto.VerifyCodeRequest;
import com.iabdinur.model.User;
import com.iabdinur.model.VerificationCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserDao userDao;
    private final VerificationCodeDao verificationCodeDao;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();
    
    // Rate limiting constants
    private static final int MAX_ATTEMPTS_PER_CODE = 5;
    private static final int MAX_CODES_PER_HOUR = 3;
    private static final int CODE_EXPIRATION_MINUTES = 10;
    
    public UserService(
            UserDao userDao,
            VerificationCodeDao verificationCodeDao,
            PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.verificationCodeDao = verificationCodeDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Optional<LoginResponse> login(LoginRequest request) {
        Optional<User> userOpt = userDao.selectUserByEmail(request.email());
        
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        
        User user = userOpt.get();
        
        // Check password using PasswordEncoder
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            return Optional.empty();
        }
        
        // Generate a simple token (in production, use JWT)
        String token = UUID.randomUUID().toString();
        
        return Optional.of(new LoginResponse(token, UserDTO.fromEntity(user)));
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> findByEmail(String email) {
        return userDao.selectUserByEmail(email)
            .map(UserDTO::fromEntity);
    }

    @Transactional
    public UserDTO createUser(String name, String email, String password) {
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(name, email, hashedPassword);
        userDao.insertUser(user);
        return UserDTO.fromEntity(user);
    }

    @Transactional
    public void sendVerificationCode(SendCodeRequest request) {
        // Check rate limiting
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        int recentCodes = verificationCodeDao.countRecentCodesByEmail(request.email(), oneHourAgo);
        
        if (recentCodes >= MAX_CODES_PER_HOUR) {
            // Don't reveal rate limit - just return success (security best practice)
            return;
        }
        
        // Find user by email
        Optional<User> userOpt = userDao.selectUserByEmail(request.email());
        
        if (userOpt.isEmpty()) {
            // Don't reveal if user exists - just return success
            return;
        }
        
        // Invalidate any existing active codes for this email
        verificationCodeDao.invalidateCode(request.email());
        
        // Generate 6-digit code
        String code = String.format("%06d", secureRandom.nextInt(1000000));
        
        // Hash the code before storing
        String hashedCode = passwordEncoder.encode(code);
        
        // Store code with 10-minute expiration
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES);
        VerificationCode verificationCode = new VerificationCode(
            request.email(),
            hashedCode,
            expiresAt
        );
        
        verificationCodeDao.insertVerificationCode(verificationCode);
        
        // Clean up expired codes periodically (could be done via scheduled task)
        verificationCodeDao.deleteExpiredCodes();
        
        // In production, send email here using an email service
        // For now, we'll just log it (you can check server logs)
        System.out.println("Verification code for " + request.email() + ": " + code);
        System.out.println("This code expires in " + CODE_EXPIRATION_MINUTES + " minutes.");
    }

    @Transactional
    public Optional<LoginResponse> verifyCode(VerifyCodeRequest request) {
        // Find active code for this email
        Optional<VerificationCode> codeOpt = verificationCodeDao.findActiveCodeByEmail(request.email());
        
        if (codeOpt.isEmpty()) {
            return Optional.empty();
        }
        
        VerificationCode verificationCode = codeOpt.get();
        
        // Check if code is expired (double check)
        if (verificationCode.isExpired()) {
            verificationCodeDao.invalidateCode(request.email());
            return Optional.empty();
        }
        
        // Check if code has exceeded max attempts
        if (verificationCode.getAttempts() >= MAX_ATTEMPTS_PER_CODE) {
            verificationCodeDao.invalidateCode(request.email());
            throw new ResponseStatusException(
                HttpStatus.TOO_MANY_REQUESTS,
                "Too many verification attempts. Please request a new code."
            );
        }
        
        // Increment attempts
        verificationCode.incrementAttempts();
        
        // Verify code using password encoder (since code is hashed)
        if (!passwordEncoder.matches(request.code(), verificationCode.getHashedCode())) {
            verificationCodeDao.updateVerificationCode(verificationCode);
            return Optional.empty();
        }
        
        // Code is valid - mark as used
        verificationCode.setIsUsed(true);
        verificationCodeDao.updateVerificationCode(verificationCode);
        
        // Find user and generate token
        Optional<User> userOpt = userDao.selectUserByEmail(request.email());
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        
        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        
        return Optional.of(new LoginResponse(token, UserDTO.fromEntity(user)));
    }
}
