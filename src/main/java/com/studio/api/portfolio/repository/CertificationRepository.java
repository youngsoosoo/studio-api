package com.studio.api.portfolio.repository;

import com.studio.api.portfolio.entity.CertificationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepository extends JpaRepository<CertificationEntity, Long> {

    List<CertificationEntity> findAllByOrderBySortOrderAsc();
}
