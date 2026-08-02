package com.studio.api.portfolio.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.List;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "experience")
@Comment("경력 내역")
public class ExperienceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("경력 식별자")
    private Long id;

    @Column(nullable = false, length = 100)
    @Comment("API에서 사용하는 고유 경력 식별 문자열")
    private String slug;

    @Column(nullable = false, length = 200)
    @Comment("회사 또는 조직명")
    private String company;

    @Column(nullable = false, length = 200)
    @Comment("직책 또는 역할")
    private String role;

    @Column(nullable = false, length = 100)
    @Comment("표시용 재직 기간")
    private String period;

    @Column(nullable = false, length = 150)
    @Comment("근무 지역")
    private String location;

    @Column(nullable = false)
    @Comment("경력 요약")
    private String summary;

    @Column(name = "sort_order", nullable = false)
    @Comment("경력 표시 순서")
    private int sortOrder;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "experience_achievement", joinColumns = @JoinColumn(name = "experience_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "content", nullable = false)
    @Comment("경력별 주요 성과 목록")
    @Comment(value = "경력 식별자", on = "experience_id")
    @Comment(value = "주요 성과 표시 순서", on = "sort_order")
    @Comment(value = "주요 성과 내용", on = "content")
    private List<String> achievements;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "experience_stack_item", joinColumns = @JoinColumn(name = "experience_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "name", nullable = false, length = 100)
    @Comment("경력별 기술 스택 목록")
    @Comment(value = "경력 식별자", on = "experience_id")
    @Comment(value = "기술 표시 순서", on = "sort_order")
    @Comment(value = "기술명", on = "name")
    private List<String> stack;

    protected ExperienceEntity() {
    }

    public ExperienceEntity(String slug, String company, String role, String period, String location,
            String summary, int sortOrder, List<String> achievements, List<String> stack) {
        this.slug = slug;
        this.company = company;
        this.role = role;
        this.period = period;
        this.location = location;
        this.summary = summary;
        this.sortOrder = sortOrder;
        this.achievements = achievements;
        this.stack = stack;
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getCompany() {
        return company;
    }

    public String getRole() {
        return role;
    }

    public String getPeriod() {
        return period;
    }

    public String getLocation() {
        return location;
    }

    public String getSummary() {
        return summary;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public List<String> getAchievements() {
        return achievements;
    }

    public List<String> getStack() {
        return stack;
    }
}
