package com.iabdinur.dto;

public record LoginResponse(
    String token,
    AccountDTO account
) {}

