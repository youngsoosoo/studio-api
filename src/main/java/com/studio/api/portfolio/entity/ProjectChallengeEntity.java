package com.studio.api.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.Comment;

/**
 * One card in the case study's "기술적 도전 및 성과" section: what was hard and
 * what came out of solving it.
 */
@Entity
@Table(name = "project_challenge")
@Comment("기존 프로젝트 단위 기술적 도전과 결과")
public class ProjectChallengeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("프로젝트 도전 과제 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    @Comment("프로젝트 상세 식별자")
    private ProjectDetailEntity detail;

    @Column(nullable = false, length = 200)
    @Comment("도전 과제 제목")
    private String title;

    @Column(nullable = false, length = 1000)
    @Comment("도전 과제와 결과 설명")
    private String description;

    @Column(name = "sort_order", nullable = false)
    @Comment("도전 과제 표시 순서")
    private int sortOrder;

    protected ProjectChallengeEntity() {
    }

    public ProjectChallengeEntity(ProjectDetailEntity detail, String title, String description, int sortOrder) {
        this.detail = detail;
        this.title = title;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
