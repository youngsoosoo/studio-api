package com.studio.api.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "education")
@Comment("학력 내역")
public class EducationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("학력 식별자")
    private Long id;

    @Column(nullable = false, length = 100)
    @Comment("API에서 사용하는 고유 학력 식별 문자열")
    private String slug;

    @Column(nullable = false, length = 200)
    @Comment("학교 또는 교육 기관명")
    private String school;

    @Column(nullable = false, length = 200)
    @Comment("학위 또는 전공 과정")
    private String degree;

    @Column(nullable = false, length = 100)
    @Comment("표시용 재학 기간")
    private String period;

    @Column(nullable = false)
    @Comment("학력 설명")
    private String description;

    @Column(name = "sort_order", nullable = false)
    @Comment("학력 표시 순서")
    private int sortOrder;

    protected EducationEntity() {
    }

    public EducationEntity(String slug, String school, String degree, String period, String description,
            int sortOrder) {
        this.slug = slug;
        this.school = school;
        this.degree = degree;
        this.period = period;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getSchool() {
        return school;
    }

    public String getDegree() {
        return degree;
    }

    public String getPeriod() {
        return period;
    }

    public String getDescription() {
        return description;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
