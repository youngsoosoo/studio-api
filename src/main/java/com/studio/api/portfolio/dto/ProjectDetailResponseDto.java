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
public record ProjectDetailResponseDto(
        String id,
        ProjectSummaryDto project,
        List<String> overview,
        String problem,
        List<ProjectProblemDto> problems,
        List<String> approach,
        List<String> contributions,
        List<ProjectChallengeDto> challenges,
        List<String> outcomes,
        List<ProjectMetricDto> metrics,
        List<String> stack,
        String team,
        List<ProjectImageDto> images,
        List<ProjectProblemCaseDto> problemCases
) {
}
