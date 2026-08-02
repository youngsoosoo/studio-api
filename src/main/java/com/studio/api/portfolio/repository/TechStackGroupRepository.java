package com.studio.api.portfolio.repository;

import com.studio.api.portfolio.entity.TechStackGroupEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechStackGroupRepository extends JpaRepository<TechStackGroupEntity, Long> {

    List<TechStackGroupEntity> findAllByOrderBySortOrderAsc();
}
