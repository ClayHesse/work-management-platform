package com.clay.wmp.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
        @NotBlank @Size(max = 50) String username,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 255) String email
) {}
