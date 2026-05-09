package com.clay.wmp.team.service;

import com.clay.wmp.common.exception.DuplicateResourceException;
import com.clay.wmp.common.exception.ResourceNotFoundException;
import com.clay.wmp.team.dto.AddTeamMemberRequest;
import com.clay.wmp.team.dto.TeamDto;
import com.clay.wmp.team.dto.TeamMapper;
import com.clay.wmp.team.dto.TeamMemberDto;
import com.clay.wmp.team.entity.Team;
import com.clay.wmp.team.entity.TeamMember;
import com.clay.wmp.team.repository.TeamMemberRepository;
import com.clay.wmp.team.repository.TeamRepository;
import com.clay.wmp.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    public TeamService(TeamRepository teamRepository, TeamMemberRepository teamMemberRepository, UserService userService, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userService = userService;
        this.userRepository = userRepository;
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

    public List<TeamMemberDto> getTeamMembersById(@PathVariable Long id) {
        return teamMemberRepository.findByTeamIdWithUser(id)
                .stream().map(TeamMapper::mapTeamMemberToTeamMemberDto).toList();
    };

    public TeamMemberDto addTeamMember(Long id, AddTeamMemberRequest addTeamMemberRequest) {
        if (teamMemberRepository.existsByTeamIdAndUserId(id, addTeamMemberRequest.userId())) {
            throw new DuplicateResourceException("Team Member already exists");
        }

        var teamMember = new TeamMember();
        var team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        teamMember.setTeam(team);

        var user = userService.getUserEntityById(addTeamMemberRequest.userId());
        teamMember.setUser(user);

        teamMember.setRole(addTeamMemberRequest.role());
        return TeamMapper.mapTeamMemberToTeamMemberDto(teamMemberRepository.save(teamMember));
    }
}
