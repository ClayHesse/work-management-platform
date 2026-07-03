package com.clay.wmp.task.repository;

import com.clay.wmp.task.entity.Task;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"project","createdBy","assignedTo"})
    List<Task> findAll();

    @EntityGraph(attributePaths = {"project","createdBy","assignedTo"})
    List<Task> findByProjectId(Long projectId);
}
