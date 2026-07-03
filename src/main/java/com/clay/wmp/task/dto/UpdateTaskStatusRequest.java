package com.clay.wmp.task.dto;

import com.clay.wmp.task.entity.Task;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(
        @NotNull Task.TaskStatus status
) {}
