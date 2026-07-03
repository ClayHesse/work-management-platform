package com.clay.wmp.project.service;

import com.clay.wmp.common.exception.ResourceNotFoundException;
import com.clay.wmp.project.dto.CreateProjectRequest;
import com.clay.wmp.project.dto.ProjectDto;
import com.clay.wmp.project.dto.ProjectMapper;
import com.clay.wmp.project.entity.Project;
import com.clay.wmp.project.repository.ProjectRepository;
import com.clay.wmp.team.service.TeamService;
import com.clay.wmp.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final static Logger log = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final TeamService teamService;

    public ProjectService(ProjectRepository projectRepository, UserService userService, TeamService teamService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.teamService = teamService;
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream().map(ProjectMapper::mapToProjectDto).toList();
    }

    @Transactional(readOnly = true)
    public ProjectDto getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(ProjectMapper::mapToProjectDto)
                .orElseThrow(() -> new ResourceNotFoundException("No Project found with that Id"));
    }

    @Transactional(readOnly = true)
    public Project getProjectEntityById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No Project found with that Id"));
    }

    @Transactional(readOnly = true)
    public boolean doesProjectExist(Long id) {
        return projectRepository.existsById(id);
    }

    @Transactional
    public ProjectDto createProject(CreateProjectRequest requestDto) {
        var project = new Project(
                requestDto.title(),
                requestDto.description(),
                userService.getUserEntityById(requestDto.ownerId()),
                (requestDto.teamId() != null) ? teamService.getTeamEntityById(requestDto.teamId()) : null);
        return ProjectMapper.mapToProjectDto(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto updateProject(Long id, ProjectDto projectDto) {
        var project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No project with Id"));

        project.setDescription(projectDto.description());
        project.setStatus(projectDto.status());
        project.setTitle(projectDto.title());
        if (project.getAssignedTeam() == null || !projectDto.teamId().equals(project.getAssignedTeam().getId())) {
            project.setAssignedTeam(teamService.getTeamEntityById(projectDto.teamId()));
        }
        if (!projectDto.ownerId().equals(project.getOwner().getId())) {
            project.setOwner(userService.getUserEntityById(projectDto.ownerId()));
        }

        return ProjectMapper.mapToProjectDto(projectRepository.save(project));
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("No Project found with that Id");
        }

        projectRepository.deleteById(id);
    }
}
