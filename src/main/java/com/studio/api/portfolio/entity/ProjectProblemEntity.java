package com.studio.api.portfolio.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.List;
import org.hibernate.annotations.Comment;

/**
 * One problem-centred case-study block. The title and description define the
 * problem, while child collections describe that problem's approach, technical
 * challenges, outcomes, and metrics.
 */
@Entity
@Table(name = "project_problem")
@Comment("프로젝트의 문제 중심 사례 연구")
public class ProjectProblemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("프로젝트 문제 사례 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    @Comment("프로젝트 상세 식별자")
    private ProjectDetailEntity detail;

    @Column(nullable = false, length = 200)
    @Comment("문제 사례 제목")
    private String title;

    @Column(nullable = false, length = 1000)
    @Comment("문제 사례 설명")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false, length = 20)
    @Comment("사례 유형 (문제 해결 / 기능 구현)")
    private ProjectProblemKind kind;

    @Column(name = "sort_order", nullable = false)
    @Comment("프로젝트 내 문제 사례 표시 순서")
    private int sortOrder;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "project_problem_approach", joinColumns = @JoinColumn(name = "problem_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "content", nullable = false, length = 2000)
    @Comment("프로젝트 문제 사례별 접근 방법 목록")
    @Comment(value = "프로젝트 문제 사례 식별자", on = "problem_id")
    @Comment(value = "접근 방법 표시 순서", on = "sort_order")
    @Comment(value = "접근 방법 내용", on = "content")
    private List<String> approach;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "project_problem_outcome", joinColumns = @JoinColumn(name = "problem_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "content", nullable = false, length = 2000)
    @Comment("프로젝트 문제 사례별 결과 목록")
    @Comment(value = "프로젝트 문제 사례 식별자", on = "problem_id")
    @Comment(value = "결과 표시 순서", on = "sort_order")
    @Comment(value = "결과 내용", on = "content")
    private List<String> outcomes;

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY)
    @OrderBy("sortOrder")
    private List<ProjectProblemChallengeEntity> challenges;

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY)
    @OrderBy("sortOrder")
    private List<ProjectProblemMetricEntity> metrics;

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY)
    @OrderBy("sortOrder")
    private List<ProjectProblemImageEntity> images;

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY)
    @OrderBy("sortOrder")
    private List<ProjectProblemVisualEntity> visuals;

    protected ProjectProblemEntity() {
    }

    public ProjectProblemEntity(
            ProjectDetailEntity detail,
            String title,
            String description,
            ProjectProblemKind kind,
            int sortOrder) {
        this.detail = detail;
        this.title = title;
        this.description = description;
        this.kind = kind;
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

    /** Rows written before the {@code kind} column existed read as PROBLEM. */
    public ProjectProblemKind getKind() {
        return kind == null ? ProjectProblemKind.PROBLEM : kind;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public List<String> getApproach() {
        return approach;
    }

    public List<String> getOutcomes() {
        return outcomes;
    }

    public List<ProjectProblemChallengeEntity> getChallenges() {
        return challenges;
    }

    public List<ProjectProblemMetricEntity> getMetrics() {
        return metrics;
    }

    public List<ProjectProblemImageEntity> getImages() {
        return images;
    }

    public List<ProjectProblemVisualEntity> getVisuals() {
        return visuals;
    }
}
