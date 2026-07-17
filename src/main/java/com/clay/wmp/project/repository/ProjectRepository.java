package com.clay.wmp.project.repository;

import com.clay.wmp.project.entity.Project;
import com.clay.wmp.user.entity.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"owner","assignedTeam"})
    List<Project> findAll();
    boolean existsByOwner_Id(Long id);
    boolean existsByAssignedTeam_Id(Long id);
}
