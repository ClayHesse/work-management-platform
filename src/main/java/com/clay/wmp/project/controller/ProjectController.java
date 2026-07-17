package com.clay.wmp.project.controller;

import com.clay.wmp.project.dto.CreateProjectRequest;
import com.clay.wmp.project.dto.ProjectDto;
import com.clay.wmp.project.dto.UpdateProjectRequest;
import com.clay.wmp.project.service.ProjectService;
import com.clay.wmp.task.dto.TaskDto;
import com.clay.wmp.task.service.TaskService;
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
@RequestMapping("/api/projects")
public class ProjectController {

    private final static Logger log = LoggerFactory.getLogger(ProjectController.class);

    private final ProjectService projectService;
    private final TaskService taskService;

    public ProjectController(ProjectService projectService,  TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ProjectDto getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody CreateProjectRequest request) {
        var project = projectService.createProject(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(project.id()).toUri();
        return ResponseEntity.created(location).body(project);
    }

    @PutMapping("/{id}")
    public ProjectDto updateProject(@PathVariable Long id,
                                    @Valid @RequestBody UpdateProjectRequest updateProjectRequest) {
        return projectService.updateProject(id, updateProjectRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/tasks")
    public List<TaskDto> getTasksByProject(@PathVariable Long id) {
        return taskService.getTasksByProjectId(id);
    }
}
