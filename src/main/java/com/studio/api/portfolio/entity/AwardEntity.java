package com.studio.api.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "award")
@Comment("포트폴리오 소유자의 수상 내역")
public class AwardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("수상 내역 식별자")
    private Long id;

    @Column(nullable = false, length = 100)
    @Comment("API에서 사용하는 고유 수상 내역 식별 문자열")
    private String slug;

    @Column(nullable = false, length = 200)
    @Comment("수상명")
    private String title;

    @Column(nullable = false, length = 200)
    @Comment("수여 기관")
    private String issuer;

    @Column(name = "date_text", nullable = false, length = 50)
    @Comment("표시용 수상 일자")
    private String dateText;

    @Column(nullable = false)
    @Comment("수상 내용 설명")
    private String description;

    @Column(name = "sort_order", nullable = false)
    @Comment("수상 내역 표시 순서")
    private int sortOrder;

    protected AwardEntity() {
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

    public String getIssuer() {
        return issuer;
    }

    public String getDateText() {
        return dateText;
    }

    public String getDescription() {
        return description;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
