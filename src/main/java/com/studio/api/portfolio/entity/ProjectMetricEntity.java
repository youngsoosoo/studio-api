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

@Entity
@Table(name = "project_metric")
@Comment("프로젝트 사례 연구 정량 지표")
public class ProjectMetricEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("프로젝트 지표 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    @Comment("프로젝트 상세 식별자")
    private ProjectDetailEntity detail;

    @Column(nullable = false, length = 100)
    @Comment("지표 이름")
    private String label;

    // "value" is a reserved word in H2, hence the column name.
    @Column(name = "metric_value", nullable = false, length = 100)
    @Comment("표시용 지표 값")
    private String metricValue;

    @Column(name = "sort_order", nullable = false)
    @Comment("지표 표시 순서")
    private int sortOrder;

    protected ProjectMetricEntity() {
    }

    public ProjectMetricEntity(ProjectDetailEntity detail, String label, String metricValue, int sortOrder) {
        this.detail = detail;
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
