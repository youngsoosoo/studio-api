package com.studio.api.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "timeline_entry")
@Comment("포트폴리오 연대기 항목")
public class TimelineEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("연대기 항목 식별자")
    private Long id;

    @Column(nullable = false, length = 100)
    @Comment("API에서 사용하는 고유 연대기 항목 식별 문자열")
    private String slug;

    @Column(name = "date_text", nullable = false, length = 50)
    @Comment("표시용 연대기 일자")
    private String dateText;

    @Column(nullable = false, length = 200)
    @Comment("연대기 항목 제목")
    private String title;

    @Column(nullable = false)
    @Comment("연대기 항목 설명")
    private String description;

    // 'work' | 'education' | 'milestone' (DB CHECK constraint).
    @Column(name = "entry_type", nullable = false, length = 20)
    @Comment("연대기 항목 유형: 경력, 학력 또는 주요 사건")
    private String entryType;

    @Column(name = "sort_order", nullable = false)
    @Comment("연대기 항목 표시 순서")
    private int sortOrder;

    protected TimelineEntryEntity() {
    }

    public TimelineEntryEntity(String slug, String dateText, String title, String description, String entryType,
            int sortOrder) {
        this.slug = slug;
        this.dateText = dateText;
        this.title = title;
        this.description = description;
        this.entryType = entryType;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getDateText() {
        return dateText;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getEntryType() {
        return entryType;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
