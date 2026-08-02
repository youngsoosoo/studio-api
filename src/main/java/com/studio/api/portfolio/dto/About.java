package com.studio.api.portfolio.dto;

import java.util.List;

/**
 * "About me" narrative section.
 */
public record About(
        String headline,
        List<String> paragraphs,
        List<String> highlights
) {
}
