package com.iabdinur.dto;

public record AuthenticationRequest(
    String email,
    String password
) {}
