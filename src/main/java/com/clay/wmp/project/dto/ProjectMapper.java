package com.clay.wmp.project.dto;

import com.clay.wmp.project.entity.Project;

public class ProjectMapper {

    public static ProjectDto mapToProjectDto(Project project) {
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
