package com.iabdinur.dto;

public record AuthenticationRequest(
    String username,
    String password
) {}
