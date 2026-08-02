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
 * An image (architecture diagram, screenshot) attached to a project's case
 * study, with display metadata.
 */
@Entity
@Table(name = "project_image")
@Comment("프로젝트 사례 연구 첨부 이미지")
public class ProjectImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("프로젝트 첨부 이미지 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    @Comment("프로젝트 상세 식별자")
    private ProjectDetailEntity detail;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "image_id")
    @Comment("첨부 이미지 식별자")
    private ImageEntity image;

    @Column(nullable = false, length = 300)
    @Comment("이미지 대체 텍스트")
    private String alt;

    @Column(length = 500)
    @Comment("선택적 이미지 설명 문구")
    private String caption;

    @Column(name = "sort_order", nullable = false)
    @Comment("프로젝트 이미지 표시 순서")
    private int sortOrder;

    protected ProjectImageEntity() {
    }

    public ProjectImageEntity(ProjectDetailEntity detail, ImageEntity image, String alt, String caption,
            int sortOrder) {
        this.detail = detail;
        this.image = image;
        this.alt = alt;
        this.caption = caption;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public ImageEntity getImage() {
        return image;
    }

    public String getAlt() {
        return alt;
    }

    public String getCaption() {
        return caption;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
