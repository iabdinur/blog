package com.iabdinur.controller;

import com.iabdinur.dto.LoginResponse;
import com.iabdinur.dto.SendCodeRequest;
import com.iabdinur.dto.UserDTO;
import com.iabdinur.dto.UserRegistrationRequest;
import com.iabdinur.dto.VerifyCodeRequest;
import com.iabdinur.service.UserService;
import com.iabdinur.util.JWTUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    
    private final UserService userService;
    private final JWTUtil jwtUtil;

    public UserController(UserService userService,
                          JWTUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/send-code")
    public ResponseEntity<Void> sendVerificationCode(@RequestBody SendCodeRequest request) {
        userService.sendVerificationCode(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public ResponseEntity<LoginResponse> verifyCode(@RequestBody VerifyCodeRequest request) {
        return userService.verifyCode(request)
            .map(response -> ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, response.token())
                .body(response))
            .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     * Register a new user.
     * Note: The primary registration flow for users is via email verification code:
     * 1. POST /users/send-code - Send verification code to email
     * 2. POST /users/verify-code - Verify code and automatically create user if needed
     * 
     * This endpoint is kept for:
     * - Admin user creation
     * - Programmatic user creation
     * - Future use cases
     */
    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserDTO createdUser = userService.createUser(request.name(), request.email(), request.password());
        String jwtToken = jwtUtil.issueToken(createdUser.email(), List.of("ROLE_USER"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .body(createdUser);
    }

    @GetMapping("{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable("email") String email) {
        return userService.findByEmail(email)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(
            value = "{email}/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public void uploadUserProfileImage(
            @PathVariable("email") String email,
            @RequestParam("file") MultipartFile file) {
        userService.uploadUserProfileImage(email, file);
    }

    @GetMapping(
            value = "{email}/profile-image",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public byte[] getUserProfileImage(@PathVariable("email") String email) {
        return userService.getUserProfileImage(email);
    }

    @PutMapping("{email}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable("email") String email,
            @RequestBody com.iabdinur.dto.UpdateUserRequest request) {
        return userService.updateUser(email, request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("{email}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable("email") String email,
            @Valid @RequestBody com.iabdinur.dto.ChangePasswordRequest request) {
        boolean changed = userService.changePassword(email, request);
        return changed 
            ? ResponseEntity.ok().build()
            : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
