package com.iabdinur.controller;

import com.iabdinur.dto.LoginRequest;
import com.iabdinur.dto.LoginResponse;
import com.iabdinur.dto.SendCodeRequest;
import com.iabdinur.dto.UserDTO;
import com.iabdinur.dto.UserRegistrationRequest;
import com.iabdinur.dto.VerifyCodeRequest;
import com.iabdinur.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/send-code")
    public ResponseEntity<Void> sendVerificationCode(@RequestBody SendCodeRequest request) {
        userService.sendVerificationCode(request);
        // Always return success to avoid email enumeration
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public ResponseEntity<LoginResponse> verifyCode(@RequestBody VerifyCodeRequest request) {
        return userService.verifyCode(request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return userService.login(request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping
    public ResponseEntity<UserDTO> register(@RequestBody UserRegistrationRequest request) {
        try {
            UserDTO createdUser = userService.createUser(request.name(), request.email(), request.password());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
