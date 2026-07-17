package com.clay.wmp.team.dto;

import com.clay.wmp.team.entity.Team;

public record TeamDto(
        Long id,
        String name
) {
    public static TeamDto fromTeam(Team team) {
        return new TeamDto(
                team.getId(),
                team.getName()
        );
    }
}
