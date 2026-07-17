package com.clay.wmp.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserPasswordRequest(
        @NotBlank @Size(max = 255) String currentPassword,
        @NotBlank @Size(min = 15, max = 255) String newPassword
) {}
