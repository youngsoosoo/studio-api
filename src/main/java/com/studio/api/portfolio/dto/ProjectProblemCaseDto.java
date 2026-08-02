package com.studio.api.portfolio.dto;

import java.util.List;

/**
 * One problem-centred case-study block. Each problem owns its definition,
 * solution steps, technical challenges, outcomes, and optional metrics.
 */
public record ProjectProblemCaseDto(
        String title,
        String problemDefinition,
        List<String> approach,
        List<ProjectChallengeDto> challenges,
        List<String> outcomes,
        List<ProjectMetricDto> metrics
) {
}
