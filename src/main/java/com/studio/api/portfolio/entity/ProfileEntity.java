package com.studio.api.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.List;
import org.hibernate.annotations.Comment;

/**
 * Single-row table (id = 1, enforced by a DB CHECK constraint) holding the
 * site owner's profile.
 */
@Entity
@Table(name = "profile")
@Comment("포트폴리오 소유자 프로필")
public class ProfileEntity {

    @Id
    @Comment("프로필 식별자; 1로 고정")
    private Long id;

    @Column(nullable = false, length = 100)
    @Comment("포트폴리오 소유자 이름")
    private String name;

    @Column(nullable = false, length = 150)
    @Comment("프로필에 표시할 직무 제목")
    private String title;

    @Column(nullable = false)
    @Comment("프로필 한 줄 소개")
    private String tagline;

    @Column(nullable = false, length = 150)
    @Comment("포트폴리오 소유자 위치")
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_image_id")
    @Comment("프로필 아바타 이미지 식별자")
    private ImageEntity avatar;

    @Column(name = "resume_url")
    @Comment("포트폴리오 소유자 이력서 URL")
    private String resumeUrl;

    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY)
    @OrderBy("sortOrder")
    private List<SocialLinkEntity> socials;

    protected ProfileEntity() {
    }

    public ProfileEntity(Long id, String name, String title, String tagline, String location, String resumeUrl) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.tagline = tagline;
        this.location = location;
        this.resumeUrl = resumeUrl;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getTagline() {
        return tagline;
    }

    public String getLocation() {
        return location;
    }

    public ImageEntity getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageEntity avatar) {
        this.avatar = avatar;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public List<SocialLinkEntity> getSocials() {
        return socials;
    }
}
