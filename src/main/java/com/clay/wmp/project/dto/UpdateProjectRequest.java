package com.clay.wmp.project.dto;

import com.clay.wmp.project.entity.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateProjectRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 2000) String description,
        @NotNull @Positive Long ownerId,
        @Positive Long teamId,
        @NotNull Project.ProjectStatus status
) {
}
