package com.studio.api.portfolio.repository;

import com.studio.api.portfolio.entity.AchievementEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<AchievementEntity, Long> {

    List<AchievementEntity> findAllByOrderBySortOrderAsc();
}
