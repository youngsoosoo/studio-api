package com.studio.api.portfolio.repository;

import com.studio.api.portfolio.entity.ExperienceEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRepository extends JpaRepository<ExperienceEntity, Long> {

    List<ExperienceEntity> findAllByOrderBySortOrderAsc();
}
