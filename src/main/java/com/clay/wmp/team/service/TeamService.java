package com.clay.wmp.team.service;

import com.clay.wmp.common.exception.DuplicateResourceException;
import com.clay.wmp.common.exception.ResourceInUseException;
import com.clay.wmp.common.exception.ResourceNotFoundException;
import com.clay.wmp.project.service.ProjectService;
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

import java.util.List;

@Service
public class TeamService {

    private static final Logger log = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectService projectService;
    private final UserService userService;

    public TeamService(TeamRepository teamRepository, TeamMemberRepository teamMemberRepository, ProjectService projectService, UserService userService) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.projectService = projectService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<TeamDto> getAllTeams() {
        return teamRepository.findAll().stream().map(TeamDto::fromTeam).toList();
    }

    @Transactional(readOnly = true)
    public TeamDto getTeamById(Long id) {
        return teamRepository.findById(id)
                .map(TeamDto::fromTeam)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
    }

    @Transactional(readOnly = true)
    public Team getTeamEntityById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
    }

    @Transactional(readOnly = true)
    public TeamDto getTeamByName(String name) {
        return teamRepository.findTeamByNameIgnoreCase(name)
                .map(TeamDto::fromTeam)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
    }

    @Transactional
    public TeamDto createTeam(CreateUpdateTeamRequest createUpdateTeamRequest) {
        if(teamRepository.existsByNameIgnoreCase(createUpdateTeamRequest.name())) {
            throw new DuplicateResourceException("Team already exists");
        }

        return TeamDto.fromTeam(teamRepository.save(new Team(createUpdateTeamRequest.name())));
    }

    @Transactional
    public TeamDto updateTeam(Long id, CreateUpdateTeamRequest createUpdateTeamRequest) {
        var team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        if(!createUpdateTeamRequest.name().equals(team.getName()) &&
                teamRepository.existsByNameIgnoreCase(createUpdateTeamRequest.name())) {
            throw new DuplicateResourceException("Team name already exists");
        }

        team.setName(createUpdateTeamRequest.name());
        return TeamDto.fromTeam(teamRepository.save(team));
    }

    @Transactional
    public void deleteTeamById(Long id) {
        if(!teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Team not found");
        }

        if (projectService.teamHasProjects(id)) {
            throw new ResourceInUseException("This team has assigned projects and cannot be deleted");
        }

        teamMemberRepository.deleteByTeam_Id(id);
        teamRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TeamMemberDto> getTeamMembersById(Long id) {
        return teamMemberRepository.findByTeamIdWithUser(id)
                .stream().map(TeamMemberDto::fromTeamMember).toList();
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
        return TeamMemberDto.fromTeamMember(teamMemberRepository.save(teamMember));
    }

    @Transactional
    public void removeMemberFromTeam(Long teamId, Long userId) {
        var member = teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Team Member not found"));

        teamMemberRepository.delete(member);
        log.info("Removed user {} from team {}", userId, teamId);
    }

    @Transactional
    public TeamMemberDto updateMemberRole(Long teamId, Long userId, UpdateTeamRoleDto  updateTeamRoleDto) {
        var teamMember = teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Team Member not found"));

        teamMember.setRole(updateTeamRoleDto.role());
        return TeamMemberDto.fromTeamMember(teamMemberRepository.save(teamMember));
    }

    public boolean isTeamMember(Long id) {
        return teamMemberRepository.existsByUser_Id(id);
    }
}
