package com.studio.api.portfolio.dto;

import java.util.List;

/**
 * A category of technologies, e.g. "Frontend", "Backend", "Infra".
 */
public record TechStackGroup(String category, List<TechItem> items) {
}
