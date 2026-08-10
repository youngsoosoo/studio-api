package com.studio.api.portfolio.repository;

import com.studio.api.portfolio.entity.ProjectProblemVisualEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectProblemVisualRepository
        extends JpaRepository<ProjectProblemVisualEntity, Long> {

    Optional<ProjectProblemVisualEntity> findByProblemIdAndSortOrder(
            Long problemId, int sortOrder);
}
