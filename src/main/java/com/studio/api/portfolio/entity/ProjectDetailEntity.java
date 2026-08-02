package com.studio.api.portfolio.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.List;
import org.hibernate.annotations.Comment;

/**
 * 1:1 case study for a project, sharing the project's primary key. A project
 * without a row here simply has no detail view.
 */
@Entity
@Table(name = "project_detail")
@Comment("프로젝트 상세 사례 연구")
public class ProjectDetailEntity {

    @Id
    @Column(name = "project_id")
    @Comment("프로젝트 테이블과 공유하는 프로젝트 식별자")
    private Long projectId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    @Column(nullable = false)
    @Comment("프로젝트가 해결한 문제 정의")
    private String problem;

    @Column(length = 300)
    @Comment("프로젝트 팀 구성 또는 협업 정보")
    private String team;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "project_overview", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "content", nullable = false)
    @Comment("프로젝트 개요 문단 목록")
    @Comment(value = "프로젝트 식별자", on = "project_id")
    @Comment(value = "개요 문단 표시 순서", on = "sort_order")
    @Comment(value = "개요 문단 내용", on = "content")
    private List<String> overview;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "project_approach", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "content", nullable = false)
    @Comment("프로젝트 구현 접근 방법 목록")
    @Comment(value = "프로젝트 식별자", on = "project_id")
    @Comment(value = "접근 방법 표시 순서", on = "sort_order")
    @Comment(value = "접근 방법 내용", on = "content")
    private List<String> approach;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "project_contribution", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "content", nullable = false)
    @Comment("프로젝트 기여 내용 목록")
    @Comment(value = "프로젝트 식별자", on = "project_id")
    @Comment(value = "기여 내용 표시 순서", on = "sort_order")
    @Comment(value = "기여 내용", on = "content")
    private List<String> contributions;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "project_outcome", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "content", nullable = false)
    @Comment("프로젝트 결과 목록")
    @Comment(value = "프로젝트 식별자", on = "project_id")
    @Comment(value = "결과 표시 순서", on = "sort_order")
    @Comment(value = "결과 내용", on = "content")
    private List<String> outcomes;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "project_detail_stack_item", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "name", nullable = false, length = 100)
    @Comment("프로젝트 상세 기술 스택 목록")
    @Comment(value = "프로젝트 식별자", on = "project_id")
    @Comment(value = "기술 표시 순서", on = "sort_order")
    @Comment(value = "기술명", on = "name")
    private List<String> stack;

    @OneToMany(mappedBy = "detail", fetch = FetchType.LAZY)
    @OrderBy("sortOrder")
    private List<ProjectMetricEntity> metrics;

    @OneToMany(mappedBy = "detail", fetch = FetchType.LAZY)
    @OrderBy("sortOrder")
    private List<ProjectImageEntity> images;

    @OneToMany(mappedBy = "detail", fetch = FetchType.LAZY)
    @OrderBy("sortOrder")
    private List<ProjectProblemEntity> problems;

    @OneToMany(mappedBy = "detail", fetch = FetchType.LAZY)
    @OrderBy("sortOrder")
    private List<ProjectChallengeEntity> challenges;

    protected ProjectDetailEntity() {
    }

    public ProjectDetailEntity(ProjectEntity project, String problem, String team, List<String> overview,
            List<String> approach, List<String> contributions, List<String> outcomes, List<String> stack) {
        this.project = project;
        this.problem = problem;
        this.team = team;
        this.overview = overview;
        this.approach = approach;
        this.contributions = contributions;
        this.outcomes = outcomes;
        this.stack = stack;
    }

    public Long getProjectId() {
        return projectId;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public String getProblem() {
        return problem;
    }

    public String getTeam() {
        return team;
    }

    public List<String> getOverview() {
        return overview;
    }

    public List<String> getApproach() {
        return approach;
    }

    public List<String> getContributions() {
        return contributions;
    }

    public List<String> getOutcomes() {
        return outcomes;
    }

    public List<String> getStack() {
        return stack;
    }

    public List<ProjectMetricEntity> getMetrics() {
        return metrics;
    }

    public List<ProjectImageEntity> getImages() {
        return images;
    }

    public List<ProjectProblemEntity> getProblems() {
        return problems;
    }

    public List<ProjectChallengeEntity> getChallenges() {
        return challenges;
    }
}
