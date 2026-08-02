package com.studio.api.portfolio.dto;

/**
 * An image in a project's case study. {@code src} is an absolute URL under
 * {@code {app.public-base-url}/files/}.
 */
public record ProjectImageDto(String src, String alt, String caption) {
}
