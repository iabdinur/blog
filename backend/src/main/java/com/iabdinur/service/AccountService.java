package com.iabdinur.service;

import com.iabdinur.dao.AccountDao;
import com.iabdinur.dto.AccountDTO;
import com.iabdinur.dto.LoginRequest;
import com.iabdinur.dto.LoginResponse;
import com.iabdinur.dto.SendCodeRequest;
import com.iabdinur.dto.VerifyCodeRequest;
import com.iabdinur.model.Account;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional(readOnly = true)
public class AccountService {
    private final AccountDao accountDao;
    private final PasswordEncoder passwordEncoder;
    
    // In-memory storage for verification codes (in production, use Redis or database)
    private final Map<String, CodeData> verificationCodes = new ConcurrentHashMap<>();
    
    private static class CodeData {
        String code;
        LocalDateTime expiresAt;
        
        CodeData(String code, LocalDateTime expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }

    public AccountService(AccountDao accountDao, PasswordEncoder passwordEncoder) {
        this.accountDao = accountDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Optional<LoginResponse> login(LoginRequest request) {
        Optional<Account> accountOpt = accountDao.selectAccountByUsername(request.username());
        
        if (accountOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Account account = accountOpt.get();
        
        // Simple password check (in production, use password hashing like BCrypt)
        if (!account.getPassword().equals(request.password())) {
            return Optional.empty();
        }
        
        // Generate a simple token (in production, use JWT)
        String token = UUID.randomUUID().toString();
        
        return Optional.of(new LoginResponse(token, AccountDTO.fromEntity(account)));
    }

    @Transactional(readOnly = true)
    public Optional<AccountDTO> findByUsername(String username) {
        return accountDao.selectAccountByUsername(username)
            .map(AccountDTO::fromEntity);
    }

    @Transactional
    public AccountDTO createAccount(String username, String password) {
        String hashedPassword = passwordEncoder.encode(password);
        Account account = new Account(username, hashedPassword);
        accountDao.insertAccount(account);
        return AccountDTO.fromEntity(account);
    }

    @Transactional
    public void sendVerificationCode(SendCodeRequest request) {
        // Find account by email (assuming username is email for now)
        Optional<Account> accountOpt = accountDao.selectAccountByUsername(request.email());
        
        if (accountOpt.isEmpty()) {
            // Don't reveal if account exists - just return success
            return;
        }
        
        // Generate 6-digit code
        String code = String.format("%06d", (int)(Math.random() * 1000000));
        
        // Store code with 10-minute expiration
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);
        verificationCodes.put(request.email(), new CodeData(code, expiresAt));
        
        // In production, send email here using an email service
        // For now, we'll just log it (you can check server logs)
        System.out.println("Verification code for " + request.email() + ": " + code);
        System.out.println("This code expires in 10 minutes.");
    }

    @Transactional
    public Optional<LoginResponse> verifyCode(VerifyCodeRequest request) {
        CodeData codeData = verificationCodes.get(request.email());
        
        if (codeData == null) {
            return Optional.empty();
        }
        
        // Check if code is expired
        if (LocalDateTime.now().isAfter(codeData.expiresAt)) {
            verificationCodes.remove(request.email());
            return Optional.empty();
        }
        
        // Verify code
        if (!codeData.code.equals(request.code())) {
            return Optional.empty();
        }
        
        // Code is valid - find account and generate token
        Optional<Account> accountOpt = accountDao.selectAccountByUsername(request.email());
        if (accountOpt.isEmpty()) {
            verificationCodes.remove(request.email());
            return Optional.empty();
        }
        
        // Remove used code
        verificationCodes.remove(request.email());
        
        Account account = accountOpt.get();
        String token = UUID.randomUUID().toString();
        
        return Optional.of(new LoginResponse(token, AccountDTO.fromEntity(account)));
    }
}

