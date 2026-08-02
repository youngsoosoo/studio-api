package com.studio.api.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "certification")
@Comment("전문 자격증 내역")
public class CertificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("자격증 식별자")
    private Long id;

    @Column(nullable = false, length = 100)
    @Comment("API에서 사용하는 고유 자격증 식별 문자열")
    private String slug;

    @Column(nullable = false, length = 200)
    @Comment("자격증명")
    private String name;

    @Column(nullable = false, length = 200)
    @Comment("자격증 발급 기관")
    private String issuer;

    @Column(name = "date_text", nullable = false, length = 50)
    @Comment("표시용 자격증 취득 일자")
    private String dateText;

    @Column(name = "credential_id", length = 100)
    @Comment("발급 기관이 부여한 자격 증명 식별자")
    private String credentialId;

    @Column(name = "sort_order", nullable = false)
    @Comment("자격증 표시 순서")
    private int sortOrder;

    protected CertificationEntity() {
    }

    public CertificationEntity(String slug, String name, String issuer, String dateText, String credentialId,
            int sortOrder) {
        this.slug = slug;
        this.name = name;
        this.issuer = issuer;
        this.dateText = dateText;
        this.credentialId = credentialId;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getDateText() {
        return dateText;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
