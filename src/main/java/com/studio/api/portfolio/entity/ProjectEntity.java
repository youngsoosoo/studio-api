package com.studio.api.portfolio.entity;

import com.studio.api.image.entity.ImageEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.List;
import org.hibernate.annotations.Comment;

/**
 * Project card data. The 1:1 case study lives in {@link ProjectDetailEntity},
 * looked up separately by project id so this entity stays free of a lazy
 * one-to-one mapping.
 */
@Entity
@Table(name = "project")
@Comment("포트폴리오 프로젝트")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("프로젝트 식별자")
    private Long id;

    @Column(nullable = false, length = 100)
    @Comment("URL과 API에서 사용하는 고유 프로젝트 식별 문자열")
    private String slug;

    @Column(nullable = false, length = 200)
    @Comment("프로젝트 제목")
    private String title;

    @Column(nullable = false)
    @Comment("프로젝트 요약")
    private String summary;

    @Column(nullable = false, length = 100)
    @Comment("프로젝트에서 수행한 역할")
    private String role;

    @Column(nullable = false, length = 100)
    @Comment("표시용 프로젝트 기간")
    private String period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_image_id")
    @Comment("프로젝트 썸네일 이미지 식별자")
    private ImageEntity thumbnail;

    @Column(name = "repo_url")
    @Comment("소스 코드 저장소 URL")
    private String repoUrl;

    @Column(name = "live_url")
    @Comment("배포된 프로젝트 URL")
    private String liveUrl;

    @Column(nullable = false)
    @Comment("대표 프로젝트 여부")
    private boolean featured;

    @Column(name = "sort_order", nullable = false)
    @Comment("프로젝트 표시 순서")
    private int sortOrder;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "project_tag", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "name", nullable = false, length = 100)
    @Comment("프로젝트별 태그 목록")
    @Comment(value = "프로젝트 식별자", on = "project_id")
    @Comment(value = "태그 표시 순서", on = "sort_order")
    @Comment(value = "태그명", on = "name")
    private List<String> tags;

    protected ProjectEntity() {
    }

    public ProjectEntity(String slug, String title, String summary, String role, String period,
            String repoUrl, String liveUrl, boolean featured, int sortOrder, List<String> tags) {
        this.slug = slug;
        this.title = title;
        this.summary = summary;
        this.role = role;
        this.period = period;
        this.repoUrl = repoUrl;
        this.liveUrl = liveUrl;
        this.featured = featured;
        this.sortOrder = sortOrder;
        this.tags = tags;
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

    public String getSummary() {
        return summary;
    }

    public String getRole() {
        return role;
    }

    public String getPeriod() {
        return period;
    }

    public ImageEntity getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ImageEntity thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public String getLiveUrl() {
        return liveUrl;
    }

    public boolean isFeatured() {
        return featured;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public List<String> getTags() {
        return tags;
    }
}
