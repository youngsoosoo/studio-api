package com.studio.api.portfolio.dto;

/**
 * A single technology within a tech-stack group.
 *
 * @param icon  short icon key the frontend maps to an SVG
 * @param level optional proficiency 1..5 (null = not shown)
 */
public record TechItem(String name, String icon, Integer level) {
}
