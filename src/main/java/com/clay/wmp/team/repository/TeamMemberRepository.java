package com.clay.wmp.team.repository;

import com.clay.wmp.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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
}
