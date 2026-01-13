package com.iabdinur.dto;

public record LoginResponse(
    String token,
    UserDTO user
) {}

