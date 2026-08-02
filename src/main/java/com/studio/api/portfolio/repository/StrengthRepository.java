package com.studio.api.portfolio.repository;

import com.studio.api.portfolio.entity.StrengthEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StrengthRepository extends JpaRepository<StrengthEntity, Long> {

    List<StrengthEntity> findAllByOrderBySortOrderAsc();
}
