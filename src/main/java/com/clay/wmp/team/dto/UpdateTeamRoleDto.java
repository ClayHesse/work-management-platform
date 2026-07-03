package com.clay.wmp.team.dto;

import com.clay.wmp.team.entity.TeamMember;
import jakarta.validation.constraints.NotNull;

public record UpdateTeamRoleDto(
        @NotNull TeamMember.TeamRole role
) {}
