package com.studio.api.portfolio.dto;

import java.util.List;

/**
 * "AboutDto me" narrative section.
 */
public record AboutDto(
        String headline,
        List<String> paragraphs,
        List<String> highlights
) {
}
