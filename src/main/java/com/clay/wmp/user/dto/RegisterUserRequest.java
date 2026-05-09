package com.clay.wmp.user.dto;

import com.clay.wmp.user.entity.User;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(
        @NotBlank String username,
        @NotBlank String name,
        @NotBlank String email,
        @NotBlank String password
) {}
