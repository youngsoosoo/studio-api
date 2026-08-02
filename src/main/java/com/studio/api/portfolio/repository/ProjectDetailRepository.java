package com.studio.api.portfolio.repository;

import com.studio.api.portfolio.entity.ProjectDetailEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectDetailRepository extends JpaRepository<ProjectDetailEntity, Long> {

    Optional<ProjectDetailEntity> findByProjectSlug(String slug);
}
