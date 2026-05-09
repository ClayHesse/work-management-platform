package com.clay.wmp.user.dto;

import com.clay.wmp.user.entity.User;

public record UserDto(
        Long id,
        String username,
        String name,
        String email,
        User.UserRole role
) {}
