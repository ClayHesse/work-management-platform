package com.clay.wmp.team.dto;

import com.clay.wmp.team.entity.TeamMember;

public record TeamMemberDto(
        Long teamId,
        String teamName,
        Long userId,
        String username,
        TeamMember.TeamRole role
) {
    public static TeamMemberDto fromTeamMember(TeamMember teamMember) {
        return new TeamMemberDto(
                teamMember.getTeam().getId(),
                teamMember.getTeam().getName(),
                teamMember.getUser().getId(),
                teamMember.getUser().getUsername(),
                teamMember.getRole()
        );
    }
}
