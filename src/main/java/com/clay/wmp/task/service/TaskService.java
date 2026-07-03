package com.clay.wmp.task.service;

import com.clay.wmp.common.exception.ResourceNotFoundException;
import com.clay.wmp.project.service.ProjectService;
import com.clay.wmp.task.dto.*;
import com.clay.wmp.task.entity.Task;
import com.clay.wmp.task.repository.TaskRepository;
import com.clay.wmp.user.entity.User;
import com.clay.wmp.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository,  ProjectService projectService,  UserService userService) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll()
                .stream().map(TaskMapper::mapTaskToDto).toList();
    }

    @Transactional(readOnly = true)
    public TaskDto getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(TaskMapper::mapTaskToDto)
                .orElseThrow(() -> new ResourceNotFoundException("No task found with this Id"));
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByProjectId(Long id) {
        if (!projectService.doesProjectExist(id)) {
            throw new ResourceNotFoundException("No project found with this Id");
        }

        return taskRepository.findByProjectId(id)
                .stream().map(TaskMapper::mapTaskToDto).toList();
    }

    @Transactional
    public TaskDto createTask(CreateTaskRequest createTaskRequest) {
        var project = projectService.getProjectEntityById(createTaskRequest.projectId());
        // createdBy will later come from security
        var createdBy =  userService.getUserEntityById(createTaskRequest.createdById());

        User assignedTo = null;
        if (createTaskRequest.assignedToId() != null) {
            assignedTo = userService.getUserEntityById(createTaskRequest.assignedToId());
        }

        var task = new Task(createTaskRequest.title(),
                createTaskRequest.description(), project, createdBy, assignedTo);

        if(createTaskRequest.priority() != null) {
            task.setPriority(createTaskRequest.priority());
        }

        return TaskMapper.mapTaskToDto(taskRepository.save(task));
    }

    @Transactional
    public TaskDto updateTask(Long id, UpdateTaskRequest updateTaskRequest) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No task found with this Id"));

        task.setTitle(updateTaskRequest.title());
        task.setDescription(updateTaskRequest.description());
        task.setStatus(updateTaskRequest.status());
        task.setPriority(updateTaskRequest.priority());

        if (updateTaskRequest.assignedToId() != null) {
            task.setAssignedTo(userService.getUserEntityById(updateTaskRequest.assignedToId()));
        }

        return TaskMapper.mapTaskToDto(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("No task found with this Id");
        }

        taskRepository.deleteById(id);
    }

    @Transactional
    public TaskDto updateTaskStatus(Long id, UpdateTaskStatusRequest updateTaskStatusRequest) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No task found with this Id"));
        task.setStatus(updateTaskStatusRequest.status());
        return TaskMapper.mapTaskToDto(taskRepository.save(task));
    }

    @Transactional
    public TaskDto assignTask(Long id, AssignTaskRequest assignTaskRequest) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No task found with this Id"));
        task.setAssignedTo(userService.getUserReferenceById(assignTaskRequest.assignedToId()));
        task.setStatus(Task.TaskStatus.ASSIGNED);
        return TaskMapper.mapTaskToDto(taskRepository.save(task));
    }
}
