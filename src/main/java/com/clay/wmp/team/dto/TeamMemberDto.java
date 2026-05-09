package com.clay.wmp.team.dto;

import com.clay.wmp.team.entity.TeamMember;

public record TeamMemberDto(
        Long teamId,
        String teamName,
        Long userId,
        String username,
        TeamMember.TeamRole role
) {
}
