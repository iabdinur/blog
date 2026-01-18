package com.iabdinur.service;

import com.iabdinur.dto.AuthenticationRequest;
import com.iabdinur.dto.AuthenticationResponse;
import com.iabdinur.dto.AuthorDTO;
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

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDTOMapper userDTOMapper;
    private final JWTUtil jwtUtil;
    private final AuthorService authorService;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 UserDTOMapper userDTOMapper,
                                 JWTUtil jwtUtil,
                                 AuthorService authorService) {
        this.authenticationManager = authenticationManager;
        this.userDTOMapper = userDTOMapper;
        this.jwtUtil = jwtUtil;
        this.authorService = authorService;
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
            
            // Check if user has an Author profile
            var authorOpt = authorService.getAuthorByEmail(userDTO.email());
            
            // Build roles list
            List<String> roles = new ArrayList<>();
            roles.add("ROLE_USER");
            if (authorOpt.isPresent()) {
                roles.add("ROLE_AUTHOR");
            }
            
            // Generate JWT token with roles
            String token = jwtUtil.issueToken(userDTO.email(), roles);
            
            // Return response with optional author
            return new AuthenticationResponse(token, userDTO, authorOpt.orElse(null));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }
}
