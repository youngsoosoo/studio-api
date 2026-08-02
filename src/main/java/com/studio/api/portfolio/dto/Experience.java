package com.studio.api.portfolio.dto;

import java.util.List;

/**
 * A single work-experience entry.
 *
 * @param period       free-form range, e.g. "2022.03 - Present"
 * @param achievements bullet points of notable outcomes
 * @param stack        technologies used in this role
 */
public record Experience(
        String id,
        String company,
        String role,
        String period,
        String location,
        String summary,
        List<String> achievements,
        List<String> stack
) {
}
