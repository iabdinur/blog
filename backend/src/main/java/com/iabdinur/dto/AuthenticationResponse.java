package com.iabdinur.dto;

public record AuthenticationResponse(
    String token,
    UserDTO user
) {}
