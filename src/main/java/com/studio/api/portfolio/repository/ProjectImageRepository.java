package com.studio.api.portfolio.repository;

import com.studio.api.portfolio.entity.ProjectImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectImageRepository extends JpaRepository<ProjectImageEntity, Long> {
}
