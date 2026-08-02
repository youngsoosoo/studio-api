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
@Table(name = "tech_item")
@Comment("포트폴리오 기술 그룹에 속한 기술 항목")
public class TechItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("기술 항목 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id")
    @Comment("기술 그룹 식별자")
    private TechStackGroupEntity group;

    @Column(nullable = false, length = 100)
    @Comment("기술명")
    private String name;

    @Column(nullable = false, length = 50)
    @Comment("클라이언트에서 사용하는 아이콘 식별자")
    private String icon;

    // Optional proficiency 1..5 (DB CHECK constraint).
    @Comment("1부터 5까지의 선택적 숙련도")
    private Integer level;

    @Column(name = "sort_order", nullable = false)
    @Comment("그룹 내 기술 표시 순서")
    private int sortOrder;

    protected TechItemEntity() {
    }

    public TechItemEntity(TechStackGroupEntity group, String name, String icon, Integer level, int sortOrder) {
        this.group = group;
        this.name = name;
        this.icon = icon;
        this.level = level;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public Integer getLevel() {
        return level;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
