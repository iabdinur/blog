package com.iabdinur.dto;

public record VerifyCodeRequest(
    String email,
    String code
) {}


