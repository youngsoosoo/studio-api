package com.studio.api.portfolio.dto;

/**
 * A point on the career/education timeline.
 *
 * @param date free-form date label, e.g. "2024.06"
 * @param type one of "work" | "education" | "milestone"
 */
public record TimelineEntry(
        String id,
        String date,
        String title,
        String description,
        String type
) {
}
