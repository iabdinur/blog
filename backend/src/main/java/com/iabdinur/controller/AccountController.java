package com.iabdinur.controller;

import com.iabdinur.dto.AccountDTO;
import com.iabdinur.dto.LoginRequest;
import com.iabdinur.dto.LoginResponse;
import com.iabdinur.dto.SendCodeRequest;
import com.iabdinur.dto.VerifyCodeRequest;
import com.iabdinur.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/send-code")
    public ResponseEntity<Void> sendVerificationCode(@RequestBody SendCodeRequest request) {
        accountService.sendVerificationCode(request);
        // Always return success to avoid email enumeration
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public ResponseEntity<LoginResponse> verifyCode(@RequestBody VerifyCodeRequest request) {
        return accountService.verifyCode(request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return accountService.login(request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping
    public ResponseEntity<AccountDTO> register(@RequestBody com.iabdinur.dto.AccountRegistrationRequest request) {
        try {
            AccountDTO createdAccount = accountService.createAccount(request.username(), request.password());
            return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(createdAccount);
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<AccountDTO> getAccountByUsername(@PathVariable String username) {
        return accountService.findByUsername(username)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}

