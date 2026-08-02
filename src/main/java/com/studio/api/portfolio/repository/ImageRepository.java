package com.studio.api.portfolio.repository;

import com.studio.api.portfolio.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
}
