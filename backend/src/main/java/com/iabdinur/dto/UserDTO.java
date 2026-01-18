package com.iabdinur.dto;

import com.iabdinur.model.UserType;

public record UserDTO(
    String id,
    String name,
    String email,
    UserType userType,
    String profileImageId,
    String createdAt,
    String updatedAt
) {
    public static UserDTO fromEntity(com.iabdinur.model.User user) {
        return new UserDTO(
            user.getId() != null ? user.getId().toString() : null,
            user.getName(),
            user.getEmail(),
            user.getUserType(),
            user.getProfileImageId(),
            user.getCreatedAt() != null ? user.getCreatedAt().toString() : null,
            user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null
        );
    }
}
