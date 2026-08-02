package com.studio.api.portfolio.dto;

/**
 * An award or notable activity ("수상 및 활동").
 *
 * @param issuer awarding organization / host
 * @param date   free-form date label, e.g. "2023.11"
 */
public record AwardDto(
        String id,
        String title,
        String issuer,
        String date,
        String description
) {
}
