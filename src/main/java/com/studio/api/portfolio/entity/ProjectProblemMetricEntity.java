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

/** One quantitative metric belonging to a problem case. */
@Entity
@Table(name = "project_problem_metric")
@Comment("프로젝트 문제 사례별 정량 지표")
public class ProjectProblemMetricEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("문제 사례 지표 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id")
    @Comment("프로젝트 문제 사례 식별자")
    private ProjectProblemEntity problem;

    @Column(nullable = false, length = 100)
    @Comment("지표 이름")
    private String label;

    @Column(name = "metric_value", nullable = false, length = 100)
    @Comment("표시용 지표 값")
    private String metricValue;

    @Column(name = "sort_order", nullable = false)
    @Comment("문제 사례 내 지표 표시 순서")
    private int sortOrder;

    protected ProjectProblemMetricEntity() {
    }

    public ProjectProblemMetricEntity(
            ProjectProblemEntity problem, String label, String metricValue, int sortOrder) {
        this.problem = problem;
        this.label = label;
        this.metricValue = metricValue;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getMetricValue() {
        return metricValue;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
