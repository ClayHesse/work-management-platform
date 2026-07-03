package com.clay.wmp.team.service;

import com.clay.wmp.common.exception.DuplicateResourceException;
import com.clay.wmp.common.exception.ResourceNotFoundException;
import com.clay.wmp.team.dto.*;
import com.clay.wmp.team.entity.Team;
import com.clay.wmp.team.entity.TeamMember;
import com.clay.wmp.team.repository.TeamMemberRepository;
import com.clay.wmp.team.repository.TeamRepository;
import com.clay.wmp.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class TeamService {

    private static final Logger log = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserService userService;

    public TeamService(TeamRepository teamRepository, TeamMemberRepository teamMemberRepository, UserService userService) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<TeamDto> getAllTeams() {
        return teamRepository.findAll().stream().map(TeamMapper::mapTeamToTeamDto).toList();
    }

    @Transactional(readOnly = true)
    public TeamDto getTeamById(Long id) {
        return teamRepository.findById(id)
                .map(TeamMapper::mapTeamToTeamDto)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
    }

    public Team getTeamEntityById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
    }

    @Transactional(readOnly = true)
    public TeamDto getTeamByName(String name) {
        return teamRepository.findTeamByNameIgnoreCase(name)
                .map(TeamMapper::mapTeamToTeamDto)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
    }

    public TeamDto createTeam(TeamDto teamDto) {
        if(teamRepository.existsByNameIgnoreCase(teamDto.name())) {
            throw new DuplicateResourceException("Team already exists");
        }

        return TeamMapper.mapTeamToTeamDto(teamRepository.save(new Team(teamDto.name())));
    }

    public TeamDto updateTeam(Long id, TeamDto teamDto) {
        var team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        if(!teamDto.name().equals(team.getName()) &&
                teamRepository.existsByNameIgnoreCase(teamDto.name())) {
            throw new DuplicateResourceException("Team name already exists");
        }

        team.setName(teamDto.name());
        return TeamMapper.mapTeamToTeamDto(teamRepository.save(team));
    }

    @Transactional
    public void deleteTeamById(Long id) {
        if(!teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Team not found");
        }

        teamRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TeamMemberDto> getTeamMembersById(@PathVariable Long id) {
        return teamMemberRepository.findByTeamIdWithUser(id)
                .stream().map(TeamMapper::mapTeamMemberToTeamMemberDto).toList();
    };

    @Transactional
    public TeamMemberDto addTeamMember(Long id, AddTeamMemberRequest addTeamMemberRequest) {
        if (teamMemberRepository.existsByTeamIdAndUserId(id, addTeamMemberRequest.userId())) {
            throw new DuplicateResourceException("Team Member already exists");
        }

        var teamMember = new TeamMember();
        var team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        teamMember.setTeam(team);

        teamMember.setUser(userService.getUserEntityById(addTeamMemberRequest.userId()));
        teamMember.setRole(addTeamMemberRequest.role());
        return TeamMapper.mapTeamMemberToTeamMemberDto(teamMemberRepository.save(teamMember));
    }

    public void removeMemberFromTeam(Long teamId, Long userId) {
        var member = teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Team Member not found"));

        teamMemberRepository.delete(member);
        log.info("Removed user {} from team {}", userId, teamId);
    }

    public TeamMemberDto updateMemberRole(Long teamId, Long userId, UpdateTeamRoleDto  updateTeamRoleDto) {
        var teamMember = teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Team Member not found"));

        teamMember.setRole(updateTeamRoleDto.role());
        return TeamMapper.mapTeamMemberToTeamMemberDto(teamMemberRepository.save(teamMember));
    }
}
