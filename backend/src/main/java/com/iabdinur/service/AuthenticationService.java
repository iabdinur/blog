package com.iabdinur.service;

import com.iabdinur.dto.AccountDTO;
import com.iabdinur.dto.AuthenticationRequest;
import com.iabdinur.dto.AuthenticationResponse;
import com.iabdinur.mapper.AccountDTOMapper;
import com.iabdinur.util.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final AccountDTOMapper accountDTOMapper;
    private final JWTUtil jwtUtil;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 AccountDTOMapper accountDTOMapper,
                                 JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.accountDTOMapper = accountDTOMapper;
        this.jwtUtil = jwtUtil;
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );
            com.iabdinur.model.AccountUserDetails principal = (com.iabdinur.model.AccountUserDetails) authentication.getPrincipal();
            AccountDTO accountDTO = accountDTOMapper.apply(principal.getAccount());
            String token = jwtUtil.issueToken(accountDTO.username(), Collections.emptyList());
            return new AuthenticationResponse(token, accountDTO);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }
}
