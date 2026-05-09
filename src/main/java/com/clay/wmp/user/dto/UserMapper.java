package com.clay.wmp.user.dto;

import com.clay.wmp.user.entity.User;

public class UserMapper {

    public static UserDto mapToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    };
}
