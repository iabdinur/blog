package com.iabdinur.dto;

public record AccountDTO(
    String id,
    String username,
    String createdAt,
    String updatedAt
) {
    public static AccountDTO fromEntity(com.iabdinur.model.Account account) {
        return new AccountDTO(
            account.getId().toString(),
            account.getUsername(),
            account.getCreatedAt() != null ? account.getCreatedAt().toString() : null,
            account.getUpdatedAt() != null ? account.getUpdatedAt().toString() : null
        );
    }
}

