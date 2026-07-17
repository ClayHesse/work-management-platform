package com.clay.wmp.user.dto;

import com.clay.wmp.user.entity.User;

public record UserDto(
        Long id,
        String username,
        String name,
        String email,
        User.UserRole role
) {
    public static UserDto fromUser(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    };
}
