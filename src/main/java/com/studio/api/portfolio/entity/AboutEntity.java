package com.studio.api.portfolio.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.List;
import org.hibernate.annotations.Comment;

/**
 * Single-row table (id = 1, enforced by a DB CHECK constraint) holding the
 * about section.
 */
@Entity
@Table(name = "about")
@Comment("포트폴리오 소개 영역")
public class AboutEntity {

    @Id
    @Comment("소개 영역 식별자; 1로 고정")
    private Long id;

    @Column(nullable = false)
    @Comment("소개 영역에 표시할 제목")
    private String headline;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "about_paragraph", joinColumns = @JoinColumn(name = "about_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "content", nullable = false)
    @Comment("소개 영역의 문단 목록")
    @Comment(value = "소개 영역 식별자", on = "about_id")
    @Comment(value = "문단 표시 순서", on = "sort_order")
    @Comment(value = "문단 내용", on = "content")
    private List<String> paragraphs;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "about_highlight", joinColumns = @JoinColumn(name = "about_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "content", nullable = false)
    @Comment("소개 영역의 강조 문구 목록")
    @Comment(value = "소개 영역 식별자", on = "about_id")
    @Comment(value = "강조 문구 표시 순서", on = "sort_order")
    @Comment(value = "강조 문구 내용", on = "content")
    private List<String> highlights;

    protected AboutEntity() {
    }

    public AboutEntity(Long id, String headline, List<String> paragraphs, List<String> highlights) {
        this.id = id;
        this.headline = headline;
        this.paragraphs = paragraphs;
        this.highlights = highlights;
    }

    public Long getId() {
        return id;
    }

    public String getHeadline() {
        return headline;
    }

    public List<String> getParagraphs() {
        return paragraphs;
    }

    public List<String> getHighlights() {
        return highlights;
    }
}
