package com.clay.wmp.task.dto;

import jakarta.validation.constraints.NotNull;

public record AssignTaskRequest(
        @NotNull Long assignedToId
) {}
