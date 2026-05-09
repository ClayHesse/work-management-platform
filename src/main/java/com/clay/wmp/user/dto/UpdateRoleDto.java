package com.clay.wmp.user.dto;

import com.clay.wmp.user.entity.User;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleDto(
        @NotNull User.UserRole role
) {}
