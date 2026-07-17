package com.clay.wmp.team.dto;

import com.clay.wmp.team.entity.TeamMember;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddTeamMemberRequest(
        @NotNull @Positive Long userId,
        @NotNull TeamMember.TeamRole role
) {}
