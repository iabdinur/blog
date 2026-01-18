package com.iabdinur.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAuthorRequest(
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    String name,
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-z0-9_-]+$", message = "Username can only contain lowercase letters, numbers, hyphens, and underscores")
    String username,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,
    
    @Size(max = 500, message = "Bio must not exceed 500 characters")
    String bio,
    
    String avatar,
    String coverImage,
    String location,
    String website,
    String github,
    String linkedin
) {}

