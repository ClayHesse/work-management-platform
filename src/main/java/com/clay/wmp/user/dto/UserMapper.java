package com.clay.wmp.user.dto;

import com.clay.wmp.user.entity.User;

public class UserMapper {

    public static UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
