package com.studio.api.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.Comment;

/**
 * Metadata for an uploaded file stored on disk under {@code app.upload.dir}.
 * The public URL is {@code {app.public-base-url}/files/{storedName}}.
 */
@Entity
@Table(name = "image")
@Comment("업로드된 이미지 파일 메타데이터")
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("이미지 메타데이터 식별자")
    private Long id;

    @Column(name = "stored_name", nullable = false, length = 255)
    @Comment("저장소에서 사용하는 고유 파일명")
    private String storedName;

    @Column(name = "original_name", nullable = false, length = 255)
    @Comment("업로드 시 제공된 원본 파일명")
    private String originalName;

    @Column(name = "content_type", nullable = false, length = 100)
    @Comment("이미지의 MIME 콘텐츠 유형")
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    @Comment("바이트 단위 이미지 파일 크기")
    private long sizeBytes;

    @Column(name = "created_at", nullable = false)
    @Comment("이미지 메타데이터 생성 일시")
    private LocalDateTime createdAt;

    protected ImageEntity() {
    }

    public ImageEntity(String storedName, String originalName, String contentType, long sizeBytes) {
        this.storedName = storedName;
        this.originalName = originalName;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getStoredName() {
        return storedName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
