package com.clay.wmp.team.dto;

import com.clay.wmp.team.entity.Team;
import com.clay.wmp.team.entity.TeamMember;

public class TeamMapper {

    public static TeamDto mapTeamToTeamDto(Team team) {
        return new TeamDto(
                team.getId(),
                team.getName()
        );
    }

    public static TeamMemberDto mapTeamMemberToTeamMemberDto(TeamMember teamMember) {
        return new TeamMemberDto(
                teamMember.getUser().getId(),
                teamMember.getUser().getUsername(),
                teamMember.getTeam().getId(),
                teamMember.getTeam().getName(),
                teamMember.getRole()
        );
    }
}
