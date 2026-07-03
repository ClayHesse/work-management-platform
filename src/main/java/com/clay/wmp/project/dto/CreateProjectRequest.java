package com.clay.wmp.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProjectRequest(
        @NotBlank String title,
        String description,
        @NotNull Long ownerId,
        Long teamId
) {}
