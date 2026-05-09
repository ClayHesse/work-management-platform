package com.clay.wmp.team.repository;

import com.clay.wmp.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findTeamByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
