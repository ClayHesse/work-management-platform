package com.clay.wmp.user.dto;

import java.util.UUID;

public record UserResponse(
        Long id,
        String username,
        String name,
        String email,
        String role
) {}
