package com.clay.wmp.team.controller;

import com.clay.wmp.team.dto.AddTeamMemberRequest;
import com.clay.wmp.team.dto.TeamDto;
import com.clay.wmp.team.dto.TeamMemberDto;
import com.clay.wmp.team.entity.Team;
import com.clay.wmp.team.service.TeamService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private static final Logger log = LoggerFactory.getLogger(TeamController.class);

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public List<TeamDto> getAllTeams() {
        log.info("Attempting to get all teams");
        return teamService.getAllTeams();
    }

    @GetMapping("/{id}")
    public TeamDto getTeamById(@PathVariable Long id) {
        log.info("Attempting to get team by id: {}", id);
        return teamService.getTeamById(id);
    }

    @GetMapping("/name/{name}")
    public TeamDto getTeamByName(@PathVariable String name) {
        log.info("Attempting to get team by name: {}", name);
        return teamService.getTeamByName(name);
    }

    @GetMapping("/{id}/members")
    public List<TeamMemberDto> getTeamMembers(@PathVariable Long id) {
        log.info("Attempting to get team members by team id: {}", id);
        return teamService.getTeamMembersById(id);
    }

    @PostMapping
    public ResponseEntity<TeamDto> createTeam(TeamDto teamDto) {
        log.info("Attempting to create team");
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(teamDto));
    }

    @PostMapping("/{id}/members")
    public TeamMemberDto addMemberToTeam(
            @PathVariable Long id, @Valid @RequestBody AddTeamMemberRequest addTeamMemberRequest) {
        log.info("Attempting to add user id {} to team id: {}", addTeamMemberRequest.userId(), id);
        return teamService.addTeamMember(id, addTeamMemberRequest);
    }

    @PutMapping("/{id}")
    public TeamDto updateTeam(@PathVariable Long id, TeamDto teamDto) {
        log.info("Attempting to update team by id: {}", id);
        return teamService.updateTeam(id, teamDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        log.info("Attempting to delete team by id: {}", id);
        teamService.deleteTeamById(id);
        return ResponseEntity.noContent().build();
    }
}
