package com.studio.api.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.List;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "tech_stack_group")
@Comment("포트폴리오 기술 분류 그룹")
public class TechStackGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("기술 그룹 식별자")
    private Long id;

    @Column(nullable = false, length = 100)
    @Comment("기술 분류명")
    private String category;

    @Column(name = "sort_order", nullable = false)
    @Comment("기술 그룹 표시 순서")
    private int sortOrder;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    @OrderBy("sortOrder")
    private List<TechItemEntity> items;

    protected TechStackGroupEntity() {
    }

    public TechStackGroupEntity(String category, int sortOrder) {
        this.category = category;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public List<TechItemEntity> getItems() {
        return items;
    }
}
