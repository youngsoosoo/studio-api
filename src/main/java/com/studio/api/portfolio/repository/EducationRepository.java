package com.studio.api.portfolio.repository;

import com.studio.api.portfolio.entity.EducationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EducationRepository extends JpaRepository<EducationEntity, Long> {

    List<EducationEntity> findAllByOrderBySortOrderAsc();
}
