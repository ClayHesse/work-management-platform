package com.clay.wmp.team.controller;

import com.clay.wmp.team.dto.*;
import com.clay.wmp.team.service.TeamService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    public ResponseEntity<TeamDto> createTeam(@Valid @RequestBody CreateUpdateTeamRequest createUpdateTeamRequest) {
        log.info("Attempting to create team");
        var team = teamService.createTeam(createUpdateTeamRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(team.id()).toUri();
        return ResponseEntity.created(location).body(team);
    }

    @PutMapping("/{id}")
    public TeamDto updateTeam(@PathVariable Long id, @Valid @RequestBody CreateUpdateTeamRequest createUpdateTeamRequest) {
        log.info("Attempting to update team by id: {}", id);
        return teamService.updateTeam(id, createUpdateTeamRequest);
    }

    @PostMapping("/{id}/members")
    public TeamMemberDto addMemberToTeam(
            @PathVariable Long id, @Valid @RequestBody AddTeamMemberRequest addTeamMemberRequest) {
        log.info("Attempting to add user id {} to team id: {}", addTeamMemberRequest.userId(), id);
        return teamService.addTeamMember(id, addTeamMemberRequest);
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeMemberFromTeam(@PathVariable Long id, @PathVariable Long userId) {
        teamService.removeMemberFromTeam(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/members/{userId}/role")
    public TeamMemberDto updateTeamMemberRole (
            @PathVariable Long id, @PathVariable Long userId, @Valid @RequestBody UpdateTeamRoleDto teamRoleDto) {
        return teamService.updateMemberRole(id, userId, teamRoleDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        log.info("Attempting to delete team by id: {}", id);
        teamService.deleteTeamById(id);
        return ResponseEntity.noContent().build();
    }
}
