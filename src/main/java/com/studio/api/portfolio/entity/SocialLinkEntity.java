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
@Table(name = "social_link")
@Comment("포트폴리오 프로필의 소셜 및 외부 링크")
public class SocialLinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("소셜 링크 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id")
    @Comment("프로필 식별자")
    private ProfileEntity profile;

    @Column(nullable = false, length = 100)
    @Comment("사용자에게 표시할 링크 이름")
    private String label;

    @Column(nullable = false)
    @Comment("이동할 URL")
    private String url;

    @Column(nullable = false, length = 50)
    @Comment("클라이언트에서 사용하는 아이콘 식별자")
    private String icon;

    @Column(name = "sort_order", nullable = false)
    @Comment("소셜 링크 표시 순서")
    private int sortOrder;

    protected SocialLinkEntity() {
    }

    public SocialLinkEntity(ProfileEntity profile, String label, String url, String icon, int sortOrder) {
        this.profile = profile;
        this.label = label;
        this.url = url;
        this.icon = icon;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }

    public String getIcon() {
        return icon;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
