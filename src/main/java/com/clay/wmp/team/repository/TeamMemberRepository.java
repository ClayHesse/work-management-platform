package com.clay.wmp.team.repository;

import com.clay.wmp.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    @Query("""
    select tm
    from TeamMember tm
    join fetch tm.user
    join fetch tm.team
    where tm.team.id = :id
    """)
    List<TeamMember> findByTeamIdWithUser(Long id);
    boolean existsByTeamIdAndUserId(Long teamId, Long userId);
    Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId);
}
