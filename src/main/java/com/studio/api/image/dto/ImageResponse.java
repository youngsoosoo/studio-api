package com.studio.api.image.dto;

/** Result of an image upload; {@code url} is the public /files/ URL. */
public record ImageResponse(
        Long id,
        String url,
        String originalName,
        String contentType,
        long sizeBytes
) {
}
