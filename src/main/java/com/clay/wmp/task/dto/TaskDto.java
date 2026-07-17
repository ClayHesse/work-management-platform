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
) {
    public static TaskDto fromTask(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getProject().getId(),
                task.getProject().getTitle(),
                task.getCreatedBy().getId(),
                task.getCreatedBy().getName(),
                (task.getAssignedTo() != null) ? task.getAssignedTo().getId() : null,
                (task.getAssignedTo() != null) ? task.getAssignedTo().getName() : null,
                task.getStatus(),
                task.getPriority(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
