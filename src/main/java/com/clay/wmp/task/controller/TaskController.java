package com.clay.wmp.task.controller;

import com.clay.wmp.task.dto.*;
import com.clay.wmp.task.service.TaskService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskDto> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public TaskDto getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody CreateTaskRequest createTaskRequest) {
        var task = taskService.createTask(createTaskRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(task.id()).toUri();
        return ResponseEntity.created(location).body(task);
    }

    @PutMapping("/{id}")
    public TaskDto updateTask(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest updateTaskRequest) {
        return taskService.updateTask(id, updateTaskRequest);
    }

    @PatchMapping("/{id}/status")
    public TaskDto updateTaskStatus(@PathVariable Long id, @Valid @RequestBody UpdateTaskStatusRequest updateTaskStatusRequest) {
        return taskService.updateTaskStatus(id, updateTaskStatusRequest);
    }

    @PatchMapping("/{id}/assignee")
    public TaskDto assignTask(@PathVariable Long id, @Valid @RequestBody AssignTaskRequest assignTaskRequest) {
        return taskService.assignTask(id, assignTaskRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
