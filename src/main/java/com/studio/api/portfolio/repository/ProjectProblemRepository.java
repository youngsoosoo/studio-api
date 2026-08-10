package com.studio.api.portfolio.repository;

import com.studio.api.portfolio.entity.ProjectProblemEntity;
import com.studio.api.portfolio.entity.ProjectProblemKind;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectProblemRepository extends JpaRepository<ProjectProblemEntity, Long> {

    @Query("""
            select problem
            from ProjectProblemEntity problem
            join problem.detail detail
            join detail.project projectEntity
            where projectEntity.slug = :projectSlug
              and problem.kind = :kind
              and problem.sortOrder = :sortOrder
            """)
    Optional<ProjectProblemEntity> findCase(
            @Param("projectSlug") String projectSlug,
            @Param("kind") ProjectProblemKind kind,
            @Param("sortOrder") int sortOrder);
}
