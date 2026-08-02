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

/** A technical challenge and result belonging to one problem case. */
@Entity
@Table(name = "project_problem_challenge")
@Comment("프로젝트 문제 사례별 기술적 도전과 결과")
public class ProjectProblemChallengeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("문제 도전 과제 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id")
    @Comment("프로젝트 문제 사례 식별자")
    private ProjectProblemEntity problem;

    @Column(nullable = false, length = 200)
    @Comment("도전 과제 제목")
    private String title;

    @Column(nullable = false, length = 1000)
    @Comment("도전 과제와 결과 설명")
    private String description;

    @Column(name = "sort_order", nullable = false)
    @Comment("문제 사례 내 도전 과제 표시 순서")
    private int sortOrder;

    protected ProjectProblemChallengeEntity() {
    }

    public ProjectProblemChallengeEntity(
            ProjectProblemEntity problem, String title, String description, int sortOrder) {
        this.problem = problem;
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
