package com.clay.wmp.task.dto;

import com.clay.wmp.task.entity.Task;

import java.time.Instant;

public record TaskDto(
        Long id,
        String title,
        String description,
        Long projectId,
        String projectName,
        Long createdById,
        String createdByName,
        Long assignedToId,
        String assignedToName,
        Task.TaskStatus status,
        Task.TaskPriority priority,
        Instant createdAt,
        Instant updatedAt
) {}
