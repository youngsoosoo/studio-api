package com.studio.api.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "strength")
@Comment("포트폴리오에 강조할 핵심 강점")
public class StrengthEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("강점 식별자")
    private Long id;

    @Column(nullable = false, length = 100)
    @Comment("API에서 사용하는 고유 강점 식별 문자열")
    private String slug;

    @Column(nullable = false, length = 200)
    @Comment("강점 제목")
    private String title;

    @Column(nullable = false)
    @Comment("강점 설명")
    private String description;

    @Column(nullable = false, length = 50)
    @Comment("클라이언트에서 사용하는 아이콘 식별자")
    private String icon;

    @Column(name = "sort_order", nullable = false)
    @Comment("강점 표시 순서")
    private int sortOrder;

    protected StrengthEntity() {
    }

    public StrengthEntity(String slug, String title, String description, String icon, int sortOrder) {
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.sortOrder = sortOrder;
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

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
