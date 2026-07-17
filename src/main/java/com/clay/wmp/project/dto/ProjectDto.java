package com.clay.wmp.project.dto;

import com.clay.wmp.project.entity.Project;

public record ProjectDto(
        Long id,
        String title,
        String description,
        Long ownerId,
        String ownerName,
        Long teamId,
        String teamName,
        Project.ProjectStatus status
) {
    public static ProjectDto fromProject(Project project) {
        return new ProjectDto(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getOwner().getId(),
                project.getOwner().getName(),
                (project.getAssignedTeam() != null) ? project.getAssignedTeam().getId() : null,
                (project.getAssignedTeam() != null) ? project.getAssignedTeam().getName() : null,
                project.getStatus()
        );
    }
}
