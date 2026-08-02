package com.studio.api.portfolio.repository;

import com.studio.api.portfolio.entity.TimelineEntryEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimelineEntryRepository extends JpaRepository<TimelineEntryEntity, Long> {

    List<TimelineEntryEntity> findAllByOrderBySortOrderAsc();
}
