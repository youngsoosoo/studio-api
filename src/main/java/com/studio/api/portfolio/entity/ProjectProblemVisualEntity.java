package com.studio.api.portfolio.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.studio.api.image.entity.ImageEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** One ordered image, flow diagram, or table attached to a project case. */
@Entity
@Table(
        name = "project_problem_visual",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_project_problem_visual_order",
                columnNames = {"problem_id", "sort_order"}))
@Comment("Declarative visual attached to an individual project problem or feature case")
public class ProjectProblemVisualEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private ProjectProblemEntity problem;

    @Enumerated(EnumType.STRING)
    @Column(name = "visual_type", nullable = false, length = 20)
    private ProjectProblemVisualType visualType;

    @Column(length = 200)
    private String title;

    @Column(length = 500)
    private String caption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private ImageEntity image;

    @Column(length = 300)
    private String alt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload")
    private JsonNode payload;

    @Column(name = "schema_version", nullable = false)
    private int schemaVersion;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    protected ProjectProblemVisualEntity() {
    }

    public static ProjectProblemVisualEntity image(
            ProjectProblemEntity problem,
            ImageEntity image,
            String title,
            String alt,
            String caption,
            int sortOrder) {
        ProjectProblemVisualEntity visual = new ProjectProblemVisualEntity();
        visual.problem = problem;
        visual.visualType = ProjectProblemVisualType.IMAGE;
        visual.title = title;
        visual.caption = caption;
        visual.image = image;
        visual.alt = alt;
        visual.payload = null;
        visual.schemaVersion = 1;
        visual.sortOrder = sortOrder;
        return visual;
    }

    public Long getId() {
        return id;
    }

    public ProjectProblemVisualType getVisualType() {
        return visualType;
    }

    public String getTitle() {
        return title;
    }

    public String getCaption() {
        return caption;
    }

    public ImageEntity getImage() {
        return image;
    }

    public String getAlt() {
        return alt;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
