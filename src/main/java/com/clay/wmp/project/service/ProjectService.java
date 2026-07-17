package com.clay.wmp.project.service;

import com.clay.wmp.common.exception.ResourceNotFoundException;
import com.clay.wmp.project.dto.CreateProjectRequest;
import com.clay.wmp.project.dto.ProjectDto;
import com.clay.wmp.project.dto.UpdateProjectRequest;
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
        return projectRepository.findAll().stream().map(ProjectDto::fromProject).toList();
    }

    @Transactional(readOnly = true)
    public ProjectDto getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(ProjectDto::fromProject)
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
        return ProjectDto.fromProject(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto updateProject(Long id, UpdateProjectRequest updateProjectRequest) {
        var project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No project with Id"));

        project.setTitle(updateProjectRequest.title());
        project.setDescription(updateProjectRequest.description());
        project.setStatus(updateProjectRequest.status());
        project.setOwner(userService.getUserEntityById(updateProjectRequest.ownerId())); //DTO owner id cannot be null

        if(updateProjectRequest.teamId() != null) {
            project.setAssignedTeam(teamService.getTeamEntityById(updateProjectRequest.teamId()));
        }
        else {
            project.setAssignedTeam(null);
        }

        return ProjectDto.fromProject(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("No Project found with that Id");
        }

        projectRepository.deleteById(id);
    }

    public boolean teamHasProjects(Long teamId) {
        return projectRepository.existsByAssignedTeam_Id(teamId);
    }

    public boolean userHasProjects(Long id) {
        return projectRepository.existsByOwner_Id(id);
    }
}
