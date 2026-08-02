package com.studio.api.portfolio.dto;

import java.util.List;

/**
 * Aggregated payload powering the entire public portfolio page in one request.
 */
public record PortfolioResponse(
        Profile profile,
        About about,
        List<Strength> strengths,
        List<Achievement> achievements,
        List<Experience> experiences,
        List<ProjectSummary> projects,
        List<TechStackGroup> techStack,
        List<Education> education,
        List<Award> awards,
        List<Certification> certifications,
        List<TimelineEntry> timeline
) {
}
