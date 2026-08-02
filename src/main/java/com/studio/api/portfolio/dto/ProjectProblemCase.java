package com.studio.api.portfolio.dto;

import java.util.List;

/**
 * One problem-centred case-study block. Each problem owns its definition,
 * solution steps, technical challenges, outcomes, and optional metrics.
 */
public record ProjectProblemCase(
        String title,
        String problemDefinition,
        List<String> approach,
        List<ProjectChallenge> challenges,
        List<String> outcomes,
        List<ProjectMetric> metrics
) {
}
