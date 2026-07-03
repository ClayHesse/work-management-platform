package com.clay.wmp.task.dto;

import com.clay.wmp.task.entity.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTaskRequest(
        @NotBlank String title,
        String description,
        @NotNull Long createdById,
        @NotNull Long projectId,
        Long assignedToId,
        Task.TaskPriority priority
) {}
