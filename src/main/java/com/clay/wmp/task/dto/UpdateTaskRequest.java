package com.clay.wmp.task.dto;

import com.clay.wmp.task.entity.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 2000) String description,
        @Positive Long assignedToId,
        @NotNull Task.TaskStatus status,
        @NotNull Task.TaskPriority priority
) {}
