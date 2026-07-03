package com.clay.wmp.task.dto;

import com.clay.wmp.task.entity.Task;

public class TaskMapper {

    public static TaskDto mapTaskToDto(Task task) {
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
