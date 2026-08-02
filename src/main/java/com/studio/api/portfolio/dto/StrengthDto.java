package com.studio.api.portfolio.dto;

/**
 * A core strength / value-proposition card.
 *
 * @param icon short icon key the frontend maps to an SVG
 */
public record StrengthDto(
        String id,
        String title,
        String description,
        String icon
) {
}
