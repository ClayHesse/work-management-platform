package com.clay.wmp.task.dto;

import com.clay.wmp.task.entity.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskRequest(
        @NotBlank String title,
        String description,
        Long assignedToId,
        @NotNull Task.TaskStatus status,
        @NotNull Task.TaskPriority priority
) {}
