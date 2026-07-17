package com.clay.wmp.user.dto;

import com.clay.wmp.user.entity.User;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(
        @NotNull User.UserRole role
) {}
