package com.studio.api.portfolio.dto;

/**
 * A key achievement / quantified outcome ("핵심 성과").
 *
 * @param metric short, quantified impact, e.g. "p95 응답 40%↓" (nullable)
 */
public record Achievement(
        String id,
        String title,
        String description,
        String metric
) {
}
