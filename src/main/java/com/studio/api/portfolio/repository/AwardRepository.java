package com.studio.api.portfolio.repository;

import com.studio.api.portfolio.entity.AwardEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwardRepository extends JpaRepository<AwardEntity, Long> {

    List<AwardEntity> findAllByOrderBySortOrderAsc();
}
