package com.studio.api.portfolio.dto;

import java.util.List;

/**
 * A project card. The first five fields (id, title, summary, role, tags)
 * mirror the frontend's existing MockProject shape so the card renders
 * unchanged; the remaining fields are an additive superset.
 */
public record ProjectSummary(
        String id,
        String title,
        String summary,
        String role,
        List<String> tags,
        String period,
        String thumbnailUrl,
        String repoUrl,
        String liveUrl,
        boolean featured
) {
}
