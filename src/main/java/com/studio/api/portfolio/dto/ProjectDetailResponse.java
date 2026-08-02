package com.studio.api.portfolio.dto;

import java.util.List;

/**
 * A project's full case study, mirroring the frontend's {@code ProjectDetail}
 * type. {@code id} is the project slug.
 *
 * <p>{@code project} embeds the card-level summary (title, role, period,
 * thumbnail, links) so the standalone detail page at {@code /projects/{id}} can
 * render its header from this one response instead of refetching the whole
 * portfolio.
 */
public record ProjectDetailResponse(
        String id,
        ProjectSummary project,
        List<String> overview,
        String problem,
        List<ProjectProblem> problems,
        List<String> approach,
        List<String> contributions,
        List<ProjectChallenge> challenges,
        List<String> outcomes,
        List<ProjectMetric> metrics,
        List<String> stack,
        String team,
        List<ProjectImage> images,
        List<ProjectProblemCase> problemCases
) {
}
