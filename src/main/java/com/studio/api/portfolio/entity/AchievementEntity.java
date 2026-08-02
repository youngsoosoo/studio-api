package com.studio.api.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "achievement")
@Comment("포트폴리오 주요 성과")
public class AchievementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("성과 식별자")
    private Long id;

    @Column(nullable = false, length = 100)
    @Comment("API에서 사용하는 고유 성과 식별 문자열")
    private String slug;

    @Column(nullable = false, length = 200)
    @Comment("성과 제목")
    private String title;

    @Column(nullable = false)
    @Comment("성과 설명")
    private String description;

    @Column(length = 200)
    @Comment("성과의 선택적 정량 지표")
    private String metric;

    @Column(name = "sort_order", nullable = false)
    @Comment("성과 표시 순서")
    private int sortOrder;

    protected AchievementEntity() {
    }

    public AchievementEntity(String slug, String title, String description, String metric, int sortOrder) {
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.metric = metric;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getMetric() {
        return metric;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
