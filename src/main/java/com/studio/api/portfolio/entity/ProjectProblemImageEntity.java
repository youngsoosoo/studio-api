package com.studio.api.portfolio.entity;

import com.studio.api.image.entity.ImageEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Comment;

/** An architecture diagram or screenshot attached to one project case. */
@Entity
@Table(
        name = "project_problem_image",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_project_problem_image_order",
                    columnNames = {"problem_id", "sort_order"}),
            @UniqueConstraint(
                    name = "uk_project_problem_image_image",
                    columnNames = {"problem_id", "image_id"})
        })
@Comment("Image attached to an individual project problem or feature case")
public class ProjectProblemImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private ProjectProblemEntity problem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "image_id", nullable = false)
    private ImageEntity image;

    @Column(nullable = false, length = 300)
    private String alt;

    @Column(length = 500)
    private String caption;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    protected ProjectProblemImageEntity() {
    }

    public ProjectProblemImageEntity(
            ProjectProblemEntity problem,
            ImageEntity image,
            String alt,
            String caption,
            int sortOrder) {
        this.problem = problem;
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
