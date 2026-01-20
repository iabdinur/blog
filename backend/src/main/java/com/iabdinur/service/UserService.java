package com.iabdinur.service;

import com.iabdinur.dao.UserDao;
import com.iabdinur.dao.VerificationCodeDao;
import com.iabdinur.dto.AuthorDTO;
import com.iabdinur.dto.ChangePasswordRequest;
import com.iabdinur.dto.LoginRequest;
import com.iabdinur.dto.LoginResponse;
import com.iabdinur.dto.SendCodeRequest;
import com.iabdinur.dto.UpdateUserRequest;
import com.iabdinur.dto.UserDTO;
import com.iabdinur.dto.VerifyCodeRequest;
import com.iabdinur.model.User;
import com.iabdinur.model.VerificationCode;
import com.iabdinur.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserDao userDao;
    private final VerificationCodeDao verificationCodeDao;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final S3Service s3Service;
    private final AuthorService authorService;
    private final JWTUtil jwtUtil;
    private final SecureRandom secureRandom = new SecureRandom();
    
    // Rate limiting constants
    private static final int MAX_ATTEMPTS_PER_CODE = 5;
    private static final int MAX_CODES_PER_HOUR = 3;
    private static final int CODE_EXPIRATION_MINUTES = 10;
    
    public UserService(
            UserDao userDao,
            VerificationCodeDao verificationCodeDao,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            AuthorService authorService,
            JWTUtil jwtUtil,
            @Autowired(required = false) S3Service s3Service) {
        this.userDao = userDao;
        this.verificationCodeDao = verificationCodeDao;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.authorService = authorService;
        this.jwtUtil = jwtUtil;
        this.s3Service = s3Service;
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
        
        // Check if user has an Author profile
        Optional<AuthorDTO> authorOpt = authorService.getAuthorByEmail(user.getEmail());
        
        // Build roles list
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        if (authorOpt.isPresent()) {
            roles.add("ROLE_AUTHOR");
        }
        
        // Generate JWT token with roles
        String token = jwtUtil.issueToken(user.getEmail(), roles);
        
        // Create response with optional author
        LoginResponse response = new LoginResponse(token, UserDTO.fromEntity(user), authorOpt.orElse(null));
        
        return Optional.of(response);
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> findByEmail(String email) {
        return userDao.selectUserByEmail(email)
            .map(UserDTO::fromEntity);
    }

    @Transactional
    public UserDTO createUser(String name, String email, String password) {
        return createUser(name, email, password, com.iabdinur.model.UserType.REA);
    }

    @Transactional
    public UserDTO createUser(String name, String email, String password, com.iabdinur.model.UserType userType) {
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(name, email, hashedPassword, userType);
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
        
        // Send verification code via email service
        emailService.sendVerificationCode(request.email(), code, CODE_EXPIRATION_MINUTES);
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
        
        // Build roles list based on user type
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        if (user.getUserType() == com.iabdinur.model.UserType.AUT) {
            roles.add("ROLE_AUTHOR");
        }
        
        // Check if user has an Author profile (for backward compatibility)
        Optional<AuthorDTO> authorOpt = Optional.empty();
        if (user.getUserType() == com.iabdinur.model.UserType.AUT) {
            authorOpt = authorService.getAuthorByEmail(user.getEmail());
        }
        
        // Generate JWT token with roles
        String token = jwtUtil.issueToken(user.getEmail(), roles);
        
        // Create response with optional author
        LoginResponse response = new LoginResponse(token, UserDTO.fromEntity(user), authorOpt.orElse(null));
        
        return Optional.of(response);
    }

    @Transactional
    public void uploadUserProfileImage(String email, MultipartFile file) {
        if (s3Service == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, 
                "S3 service is not configured. Please provide AWS credentials.");
        }

        User user = userDao.selectUserByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        try {
            String key = s3Service.generateKey("profile-images/", file.getOriginalFilename());
            s3Service.putObject(key, file.getBytes(), file.getContentType());
            
            user.setProfileImageId(key);
            userDao.updateUser(user);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Failed to upload profile image", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] getUserProfileImage(String email) {
        if (s3Service == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, 
                "S3 service is not configured. Please provide AWS credentials.");
        }

        User user = userDao.selectUserByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        if (user.getProfileImageId() == null || user.getProfileImageId().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile image not found");
        }
        
        return s3Service.getObject(user.getProfileImageId());
    }

    @Transactional
    public void deleteUserProfileImage(String email) {
        if (s3Service == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, 
                "S3 service is not configured. Please provide AWS credentials.");
        }

        User user = userDao.selectUserByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        if (user.getProfileImageId() != null && !user.getProfileImageId().isEmpty()) {
            try {
                // Delete from S3
                s3Service.deleteObject(user.getProfileImageId());
            } catch (Exception e) {
                // Log error but continue to remove reference from database
                // In case S3 deletion fails, we still want to remove the reference
            }
            
            // Remove reference from database
            user.setProfileImageId(null);
            userDao.updateUser(user);
        }
    }

    @Transactional
    public Optional<UserDTO> updateUser(String email, UpdateUserRequest request) {
        Optional<User> userOpt = userDao.selectUserByEmail(email);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();
        
        // Update name if provided
        if (request.name() != null && !request.name().trim().isEmpty()) {
            user.setName(request.name().trim());
        }
        
        // Update email if provided and different
        if (request.email() != null && !request.email().trim().isEmpty() && !request.email().equals(email)) {
            // Check if new email already exists
            if (userDao.existsUserWithEmail(request.email().trim())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
            }
            user.setEmail(request.email().trim());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        userDao.updateUser(user);
        
        return Optional.of(UserDTO.fromEntity(user));
    }

    @Transactional
    public boolean changePassword(String email, ChangePasswordRequest request) {
        Optional<User> userOpt = userDao.selectUserByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        
        // Verify current password
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        // Update password
        String encodedPassword = passwordEncoder.encode(request.newPassword());
        user.setPassword(encodedPassword);
        user.setUpdatedAt(LocalDateTime.now());
        userDao.updateUser(user);
        
        return true;
    }
}
