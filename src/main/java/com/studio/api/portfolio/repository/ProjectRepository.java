package com.studio.api.portfolio.repository;

import com.studio.api.portfolio.entity.ProjectEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    List<ProjectEntity> findAllByOrderBySortOrderAsc();

    Optional<ProjectEntity> findBySlug(String slug);
}
