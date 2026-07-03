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
) {}
