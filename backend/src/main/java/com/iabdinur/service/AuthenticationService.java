package com.iabdinur.service;

import com.iabdinur.dto.AuthenticationRequest;
import com.iabdinur.dto.AuthenticationResponse;
import com.iabdinur.dto.UserDTO;
import com.iabdinur.mapper.UserDTOMapper;
import com.iabdinur.model.User;
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
    private final UserDTOMapper userDTOMapper;
    private final JWTUtil jwtUtil;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 UserDTOMapper userDTOMapper,
                                 JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDTOMapper = userDTOMapper;
        this.jwtUtil = jwtUtil;
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
            User principal = (User) authentication.getPrincipal();
            UserDTO userDTO = userDTOMapper.apply(principal);
            String token = jwtUtil.issueToken(userDTO.email(), Collections.emptyList());
            return new AuthenticationResponse(token, userDTO);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }
}
